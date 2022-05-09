package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.jlab.jaws.entity.IntermediateMonolog;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * A Producer provides default properties values for CLIENT_ID, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 * A default send method is also provided.
 */
public class MonologProducer extends JAWSProducer<String, IntermediateMonolog> {
    /**
     * The topic name *intermediate-registration*.
     */
    public static final String INTERMEDIATE_REGISTRATION_TOPIC = "intermediate-registration";

    /**
     * The topic name *intermediate-activation*.
     */
    public static final String INTERMEDIATE_ACTIVATION_TOPIC = "intermediate-activation";

    /**
     * The topic name *intermediate-latch*.
     */
    public static final String INTERMEDIATE_LATCH_TOPIC = "intermediate-latch";

    /**
     * The topic name *intermediate-oneshot*.
     */
    public static final String INTERMEDIATE_ONESHOT_TOPIC = "intermediate-oneshot";

    /**
     * The topic name *intermediate-mask*.
     */
    public static final String INTERMEDIATE_MASK_TOPIC = "intermediate-mask";

    /**
     * The topic name *intermediate-ondely*.
     */
    public static final String INTERMEDIATE_ONDELAY_TOPIC = "intermediate-ondelay";

    /**
     * The topic name *intermediate-offdelay*.
     */
    public static final String INTERMEDIATE_OFFDELAY_TOPIC = "intermediate-offdelay";

    /**
     * Create a new Producer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public MonologProducer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(ProducerConfig.CLIENT_ID_CONFIG, "monolog-producer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        defaults.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");

        defaults.putAll(overrides);

        return defaults;
    }

    /**
     * Send a message using the default topic, headers, timestamp, and partition.
     *
     * @param topic The intermediate monolog topic name
     * @param key The message key
     * @param value The message value
     * @return An asynchronous call Future reference
     */
    public Future<RecordMetadata> send(String topic, String key, IntermediateMonolog value) {

        Iterable<Header> headers = getDefaultHeaders();

        return this.send(new ProducerRecord<>(topic, null, null, key, value, headers));
    }
}
