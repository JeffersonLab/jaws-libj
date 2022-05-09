package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.jlab.jaws.entity.EffectiveRegistration;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * A Producer provides default properties values for CLIENT_ID, TOPIC, KEY_DESERIALIZER, and VALUE_DESERIALIZER.
 * A default send method is also provided.
 */
public class EffectiveRegistrationProducer extends JAWSProducer<String, EffectiveRegistration> {
    /**
     * The topic name
     */
    public static final String TOPIC = "effective-registrations";

    /**
     * Create a new Producer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public EffectiveRegistrationProducer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        if(overrides == null) {
            overrides = new Properties();
        }

        defaults.put(ProducerConfig.CLIENT_ID_CONFIG, "effective-registration-producer" + Instant.now().toString() + "-" + Math.random());
        defaults.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        defaults.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");

        defaults.putAll(overrides);

        return defaults;
    }

    /**
     * Send a message using the default topic, headers, timestamp, and partition.
     *
     * @param key The message key
     * @param value The message value
     * @return An asynchronous call Future reference
     */
    public Future<RecordMetadata> send(String key, EffectiveRegistration value) {

        Iterable<Header> headers = getDefaultHeaders();

        return this.send(new ProducerRecord<>(TOPIC, null, null, key, value, headers));
    }
}
