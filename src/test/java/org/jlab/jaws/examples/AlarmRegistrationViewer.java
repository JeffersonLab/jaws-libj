package org.jlab.jaws.examples;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.jlab.jaws.entity.AlarmRegistration;
import org.jlab.jaws.eventsource.EventSourceConfig;
import org.jlab.jaws.eventsource.EventSourceListener;
import org.jlab.jaws.eventsource.EventSourceRecord;
import org.jlab.jaws.eventsource.EventSourceTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class AlarmRegistrationViewer {

    private static final Logger log = LoggerFactory.getLogger(AlarmRegistrationViewer.class);

    public static void main(String[] args) throws InterruptedException {
        final String servers = args[0];

        final Properties props = new Properties();

        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "registered-alarms");
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, servers);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://registry:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,"true");

        try (final EventSourceTable<String, AlarmRegistration> table = new EventSourceTable<>(props, -1)) {

            table.addListener(new EventSourceListener<String, AlarmRegistration>() {
                @Override
                public void batch(LinkedHashMap<String, EventSourceRecord<String, AlarmRegistration>> records) {
                    for (EventSourceRecord<String, AlarmRegistration> record : records.values()) {
                        String key = record.getKey();
                        AlarmRegistration value = record.getValue();
                        System.out.println(key + "=" + value);
                    }
                }
            });

            table.start();

            table.awaitHighWaterOffset(5, TimeUnit.SECONDS);
        }
    }
}

