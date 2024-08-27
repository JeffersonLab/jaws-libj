package org.jlab.jaws.clients;

import org.jlab.jaws.entity.Alarm;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A Consumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 */
public class AlarmConsumer extends JAWSConsumer<String, Alarm> {
    /**
     * Create a new Consumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public AlarmConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(EventSourceConfig.GROUP_ID_CONFIG, "alarm-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.TOPIC_CONFIG, AlarmProducer.TOPIC);
        defaults.put(EventSourceConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
