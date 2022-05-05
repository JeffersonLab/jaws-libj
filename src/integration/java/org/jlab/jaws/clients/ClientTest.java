package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.jlab.jaws.entity.AlarmLocation;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * NOTE: Since we're not providing a BOOTSTRAP_SERVERS or SCHEMA_REGISTRY override property the environment variables
 * of the same name are by default consulted, and failing that, the defaults of localhost:9092 and http://localhost:8081
 * are used.
 */
public class ClientTest {
    @Test
    public void categoryTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, String>> results = new LinkedHashMap<>();

        try(CategoryConsumer consumer = new CategoryConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, String>> records) {
                    results.putAll(records);
                }
            });

            try(CategoryProducer producer = new CategoryProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", "");

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);
        } finally {
            // Cleanup
            try(CategoryProducer producer = new CategoryProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void locationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> results = new LinkedHashMap<>();

        try(LocationConsumer consumer = new LocationConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> records) {
                    results.putAll(records);
                }
            });

            try(LocationProducer producer = new LocationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", new AlarmLocation(null));

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);
        } finally {
            // Cleanup
            try(LocationProducer producer = new LocationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }

        Assert.assertEquals(1, results.size());
    }
}
