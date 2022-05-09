package org.jlab.jaws.clients;

import org.jlab.jaws.entity.IntermediateMonolog;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

/**
 * A Consumer provides default properties values for GROUP, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 *
 * Defaults to the *intermediate-registration* topic.  Use property key *event.source.topic* to override. See
 * *MonologProducer* for list of intermediate monolog topic names.
 */
public class MonologConsumer extends JAWSConsumer<String, IntermediateMonolog> {
    /**
     * Create a new Consumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public MonologConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(EventSourceConfig.EVENT_SOURCE_GROUP, "monolog-consumer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "intermediate-registration");
        defaults.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaults.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        defaults.putAll(overrides);

        return defaults;
    }
}
