package org.jlab.jaws.clients;

import org.jlab.jaws.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A CategoryConsumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class CategoryConsumer extends JAWSConsumer {
    /**
     * Create a new CategoryConsumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public CategoryConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "category-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "alarm-categories");
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
