package org.jlab.jaws.clients;

import org.jlab.jaws.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A InstanceConsumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class InstanceConsumer extends JAWSConsumer {
    /**
     * Create a new InstanceConsumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public InstanceConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "instance-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "alarm-instances");
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
