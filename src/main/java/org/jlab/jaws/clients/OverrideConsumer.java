package org.jlab.jaws.clients;

import org.jlab.jaws.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * An OverrideConsumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class OverrideConsumer extends JAWSConsumer {
    /**
     * Create a new OverrideConsumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public OverrideConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "override-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "alarm-overrides");
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}