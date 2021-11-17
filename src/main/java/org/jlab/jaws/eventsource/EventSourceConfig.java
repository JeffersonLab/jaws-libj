package org.jlab.jaws.eventsource;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * Configuration required to run an EventSourceTable.
 */
public class EventSourceConfig extends AbstractConfig {
    public static final String EVENT_SOURCE_TOPIC = "event.source.topic";
    public static final String EVENT_SOURCE_POLL_MILLIS = "poll.ms";
    public static final String EVENT_SOURCE_MAX_BATCH_DELAY = "max.batch.delay";
    public static final String EVENT_SOURCE_FLUSH_BATCH_THRESHOLD = "flush.batch.threshold";

    // Use the same identifiers as ConsumerConfig as we'll pass 'em right on through
    public static final String EVENT_SOURCE_GROUP = "group.id";
    public static final String EVENT_SOURCE_BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String EVENT_SOURCE_MAX_POLL_RECORDS = "max.poll.records";
    public static final String EVENT_SOURCE_KEY_DESERIALIZER = "key.deserializer";
    public static final String EVENT_SOURCE_VALUE_DESERIALIZER = "value.deserializer";

    public EventSourceConfig(Map originals) {
        super(configDef(), originals, false);
    }

    protected static ConfigDef configDef() {
        return new ConfigDef()
                .define(EVENT_SOURCE_TOPIC,
                        ConfigDef.Type.STRING,
                        "event-source",
                        ConfigDef.Importance.HIGH,
                        "Name of Kafka event source topic to monitor")
                .define(EVENT_SOURCE_POLL_MILLIS,
                        ConfigDef.Type.LONG,
                        1000l,
                        ConfigDef.Importance.HIGH,
                        "Milliseconds between polls for topic changes - notification delay is EVENT_SOURCE_MAX_BATCH_DELAY times this value with a constant stream of changes, or twice this value when changes are intermittent since the consumer thread waits for 'no changes' poll response before notifying listeners")
                .define(EVENT_SOURCE_MAX_BATCH_DELAY,
                        ConfigDef.Type.LONG,
                        1l,
                        ConfigDef.Importance.HIGH,
                        "Max number of polls before forcing a flush.  The default value of 1 results in flushes after every poll, disabling the delay.  Larger numbers introduce latency but allow accumulation of changes into larger batches (not to exceed max.poll.records).  Be aware that too high a value may result in very long pauses if there is a constant stream of new events.")
                .define(EVENT_SOURCE_FLUSH_BATCH_THRESHOLD,
                        ConfigDef.Type.LONG,
                        500l,
                        ConfigDef.Importance.HIGH,
                        "Min number of records to batch before flush.  Larger numbers introduce latency but allow accumulation of changes into larger batches.  Be aware that too high a value may result in very long pauses if there is a constant stream of new events.")
                .define(EVENT_SOURCE_GROUP,
                        ConfigDef.Type.STRING,
                        "event-source",
                        ConfigDef.Importance.HIGH,
                        "Name of Kafka consumer group to use when monitoring the EVENT_SOURCE_TOPIC")
                .define(EVENT_SOURCE_BOOTSTRAP_SERVERS,
                        ConfigDef.Type.STRING,
                        "localhost:9092",
                        ConfigDef.Importance.HIGH,
                        "Comma-separated list of host and port pairs that are the addresses of the Kafka brokers used to query the EVENT_SOURCE_TOPIC")
                .define(EVENT_SOURCE_MAX_POLL_RECORDS,
                        ConfigDef.Type.LONG,
                        "500",
                        ConfigDef.Importance.MEDIUM,
                        "The maximum number of records returned in a single call to poll(), internally.")
                .define(EVENT_SOURCE_KEY_DESERIALIZER,
                        ConfigDef.Type.STRING,
                        "org.apache.kafka.common.serialization.StringDeserializer",
                        ConfigDef.Importance.HIGH,
                        "Class name of deserializer to use for the key")
                .define(EVENT_SOURCE_VALUE_DESERIALIZER,
                        ConfigDef.Type.STRING,
                        "org.apache.kafka.common.serialization.StringDeserializer",
                        ConfigDef.Importance.HIGH,
                        "Class name of deserializer to use for the value");
    }
}
