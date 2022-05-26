package org.jlab.jaws.clients;

import org.jlab.jaws.entity.EffectiveRegistration;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A Consumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class EffectiveRegistrationConsumer extends JAWSConsumer<String, EffectiveRegistration> {
    /**
     * Create a new Consumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public EffectiveRegistrationConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "effective-registration-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, EffectiveRegistrationProducer.TOPIC);
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}