package org.jlab.jaws.clients;

import org.jlab.jaws.entity.AlarmOverrideUnion;
import org.jlab.jaws.entity.OverriddenAlarmKey;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * An OverrideConsumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class OverrideConsumer extends JAWSConsumer<OverriddenAlarmKey, AlarmOverrideUnion> {
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

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "override-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "alarm-overrides");
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
