package org.jlab.jaws.eventsource;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class EventSourceTest {
    private static Logger LOGGER = LoggerFactory.getLogger(EventSourceTest.class);

    @ClassRule
    public static Network network = Network.newNetwork();

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1"))
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("kafka"))
            .withCreateContainerCmdModifier(cmd -> cmd.withHostName("kafka").withName("kafka"));

    @Test
    public void basicTableTest() throws ExecutionException, InterruptedException, TimeoutException {

        final String topicName = "testing";

        // Admin
        AdminClient adminClient = AdminClient.create(ImmutableMap.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()
        ));

        Collection<NewTopic> topics = Collections.singletonList(new NewTopic(topicName, 1, (short) 1));

        adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);




        // Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                new StringSerializer(),
                new StringSerializer()
        );

        producer.send(new ProducerRecord<>(topicName, "key1", "value1")).get();
        producer.send(new ProducerRecord<>(topicName, "key1", "value2")).get();


        // EventSourceTable (Consumer)
        Properties props = new Properties();

        props.setProperty(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, kafka.getBootstrapServers());
        props.setProperty(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, StringDeserializer.class.getName());
        props.setProperty(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, StringDeserializer.class.getName());
        props.setProperty(EventSourceConfig.EVENT_SOURCE_TOPIC, topicName);

        EventSourceTable<String, String> table = new EventSourceTable<>(props);

        final Set<EventSourceRecord<String,String>> database = new HashSet<>();

        table.addListener(new EventSourceListener<String, String>() {
            @Override
            public void initialState(Set<EventSourceRecord<String, String>> records) {
                database.addAll(records);
                System.out.println("initialState: ");
                for(EventSourceRecord record: records) {
                    System.out.println("Record: " + record);
                }
            }
        });

        table.start();

        Thread.sleep(5000);

        assertEquals(1, database.size());
    }
}
