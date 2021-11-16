package org.jlab.jaws.eventsource;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An interface to Apache Kafka that models a topic as a database table.
 * <p>
 * We're using the term Event Sourcing casually to mean persisting state by replaying a stream of saved change events.
 * There are much more complicated, confusing, and nuanced definitions that we're surely stomping all over.
 * </p>
 * <p>
 * This class is similar to KTable in that the most recent record key determines the current record value.  It differs
 * in that it ignores the server persisted client log position (offset) and always rewinds a topic to the beginning
 * (or a specified resume offset) and replays all messages
 * every run and it notifies listeners once
 * the high water mark (highest message index) is reached.  It also differs in that it does not cache the full state
 * of the table.  It also collapses duplicate keys (newer keys replace older keys), such that intermediate results are
 * not provided (WARNING: this may be inappropriate for some use-cases).   It's useful for clients which replay data
 * frequently and are not concerned about scalability, reliability, or intermediate results
 * (transient batch processing of entire final state).
 *
 * It is not part of the Kafka Streams API and requires none of
 * that run-time scaffolding.
 * </p>
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public class EventSourceTable<K, V> extends Thread implements AutoCloseable {

    private final Logger log = LoggerFactory.getLogger(EventSourceTable.class);

    private final KafkaConsumer<K, V> consumer;
    private final EventSourceConfig config;
    private final Set<EventSourceListener<K, V>> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private long endOffset = 0;
    private boolean endReached = false;
    private long resumeOffset;

    private AtomicReference<CONSUMER_STATE> consumerState = new AtomicReference<>(CONSUMER_STATE.INITIALIZING);

    // Ordered and unique changes since last listener notification are tracked
    private final LinkedHashMap<K, EventSourceRecord<K, V>> state = new LinkedHashMap<>();

    /**
     * Create a new EventSourceTable.
     *
     * @param props The properties - see EventSourceConfig
     * @param resumeOffset The offset to resume, or -1 to start from the beginning
     */
    public EventSourceTable(Properties props, long resumeOffset) {

        this.resumeOffset = resumeOffset;

        config = new EventSourceConfig(props);

        // Not sure if there is a better way to get configs (with defaults) from EventSourceConfig into a
        // Properties object (or Map) for KafkaConsumer - we manually copy values over into a new clean Properties.
        // Tried the following without success:
        // - if you use config.valuesWithPrefixOverride() to obtain consumer props it will compile, but serialization
        // may fail at runtime w/ClassCastException! (I guess stuff is mangled from String to Objects or something)
        // - if you simply pass the constructor argument above "props" along to KafkaConsumer, the defaults for missing
        // values won't be set.
        Properties consumerProps = new Properties();

        // Pass values in as is from user (without defaults); then next we'll ensure defaults are used if needed
        // Note: using new Properties(props) does NOT work as that sets the defaults field inside the Properties object,
        // which are not carried over later
        // inside the KafkaConsumer constructor when it also creates a new Properties and uses putAll().
        consumerProps.putAll(props);

        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS));
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, config.getString(EventSourceConfig.EVENT_SOURCE_GROUP));
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, config.getString(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER));
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, config.getString(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER));

        // Deserializer specific configs are passed in via putAll(props) and don't have defaults in EventSourceConfig
        // Examples:
        // - KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG
        // - KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG


        consumer = new KafkaConsumer<K, V>(consumerProps);
    }

    public void addListener(EventSourceListener<K, V> listener) {
        listeners.add(listener);
    }

    public void removeListener(EventSourceListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        boolean transitioned = consumerState.compareAndSet(CONSUMER_STATE.INITIALIZING, CONSUMER_STATE.RUNNING);

        if(!transitioned) {
            log.debug("We must already be closed!");
        }

        if(transitioned) { // Only allow the first time!
            try {
                init();
                monitorChanges();
            } catch(WakeupException e) {
                // We expect this when CLOSED (since we call consumer.wakeup()), else throw
                if(consumerState.get() != CONSUMER_STATE.CLOSED) throw e;
            } finally {
                consumer.close();
            }
        }
    }

    private void init() {
        log.debug("subscribing to topic: {}", config.getString(EventSourceConfig.EVENT_SOURCE_TOPIC));

        consumer.subscribe(Collections.singletonList(config.getString(EventSourceConfig.EVENT_SOURCE_TOPIC)), new ConsumerRebalanceListener() {

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.debug("Seeking to beginning of topic");

                if(partitions.size() != 1) {
                    throw new IllegalStateException("We only support single partition Event Sourced topics at this time");
                }

                TopicPartition p = partitions.iterator().next(); // Exactly one partition verified above

                if(resumeOffset < 0) {
                    consumer.seekToBeginning(partitions);
                } else {
                    consumer.seek(p, resumeOffset);
                }

                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);

                endOffset = endOffsets.get(p);

                if(endOffset == 0) {
                    log.debug("Empty topic to begin with");
                    endReached = true;
                }
            }
        });

        // Note: first poll triggers seek to beginning (so some empty polls are expected)
        int emptyPollCount = 0;

        while(!endReached && consumerState.get() == CONSUMER_STATE.RUNNING) {

            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(config.getLong(EventSourceConfig.EVENT_SOURCE_POLL_MILLIS)));

            log.debug("found " + records.count() + " records");

            for (ConsumerRecord<K, V> record : records) {
                updateState(record);

                log.debug("Looking for last index: {}, found: {}", endOffset, record.offset() + 1);

                if(record.offset() + 1 == endOffset) {
                    log.debug("end of partition {} reached", record.partition());
                    endReached = true;
                }
            }

            if(records.count() == 0) {
                emptyPollCount++;
            }

            if(emptyPollCount > 10) {
                // Scenario where topic compaction is not configured properly.
                // Last message (highest endOffset) deleted before consumer connected (so never received)!
                // It is also possible server just isn't delivering messages timely, so this may be bad check...
                throw new RuntimeException("Took too long to obtain initial list; verify topic compact policy!");
            }
        }

        notifyListenersInitial(); // we always notify even if changes is empty - this tells clients initial state

        log.debug("Done with EventSourceConsumer init");
    }

    private void monitorChanges() {
        // We wait until changes have settled to avoid notifying listeners of individual changes
        // We use a simple strategy of waiting for a single poll without changes to flush
        // But we won't let changes build up too long either so we check for max poll with changes
        int pollsWithChangesSinceLastFlush = 0;
        boolean hasChanges = false;

        while(consumerState.get() == CONSUMER_STATE.RUNNING) {
            log.debug("polling for changes ({})", config.getString(EventSourceConfig.EVENT_SOURCE_TOPIC));
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(config.getLong(EventSourceConfig.EVENT_SOURCE_POLL_MILLIS)));

            if (records.count() > 0) { // We have changes
                for(ConsumerRecord<K, V> record: records) {
                    updateState(record);
                }

                log.debug("Change in topic: request update once settled");
                hasChanges = true;
            } else {
                if(hasChanges) {
                    log.debug("Flushing changes since we've settled (we had a poll with no changes)");
                    notifyListenersChanges();
                    hasChanges = false;
                    pollsWithChangesSinceLastFlush = 0;
                }
            }

            // This is an escape hatch in case poll consistently returns changes; otherwise we'd never flush!
            if(pollsWithChangesSinceLastFlush >= config.getLong(EventSourceConfig.EVENT_SOURCE_MAX_POLL_BEFORE_FLUSH)) {
                log.debug("Flushing changes due to max poll with changes");
                notifyListenersChanges();
                hasChanges = false;
                pollsWithChangesSinceLastFlush = 0;
            }

            if(hasChanges) {
                pollsWithChangesSinceLastFlush++;
            }
        }
    }

    private void notifyListenersInitial() {
        for(EventSourceListener<K, V> listener: listeners) {
            listener.initialState(new LinkedHashMap<>(state));
        }
        state.clear();
    }

    private void notifyListenersChanges() {
        for(EventSourceListener<K, V> listener: listeners) {
            listener.changes(new LinkedHashMap<>(state));
        }
        state.clear();
    }

    private void updateState(ConsumerRecord<K, V> record) {
        log.debug("State update: {}={}", record.key(), record.value());

        EventSourceRecord<K, V> esr = new EventSourceRecord<>(record.key(), record.value(), record.offset(), record.timestamp());
        state.put(record.key(), esr);
    }

    @Override
    public void close() {
        CONSUMER_STATE previousState = consumerState.getAndSet(CONSUMER_STATE.CLOSED);

        if(previousState == CONSUMER_STATE.INITIALIZING) { // start() never called!
            consumer.close();
        } else {
            consumer.wakeup(); // tap on shoulder and it'll eventually notice consumer state now CLOSED
        }
    }

    private enum CONSUMER_STATE {
        INITIALIZING, RUNNING, CLOSED
    }
}
