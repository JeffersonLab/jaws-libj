package org.jlab.jaws.clients;

import org.jlab.jaws.entity.AlarmAction;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A Consumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class ActionConsumer extends JAWSConsumer<String, AlarmAction> {
    /**
     * Create a new Consumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public ActionConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(EventSourceConfig.GROUP_ID_CONFIG, "action-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.TOPIC_CONFIG, ActionProducer.TOPIC);
        defaults.put(EventSourceConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
