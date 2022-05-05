package org.jlab.jaws.clients;

import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: Since we're not providing a BOOTSTRAP_SERVERS or SCHEMA_REGISTRY override property the environment variables
 * of the same name are by default consulted, and failing that, the defaults of localhost:9092 and http://localhost:8081
 * are used.
 */
public class ClientTest {
    @Test
    public void categoryTest() throws InterruptedException {
        CategoryConsumer consumer = new CategoryConsumer(null);

        LinkedHashMap<String, EventSourceRecord<String, String>> results = new LinkedHashMap<>();

        consumer.addListener(new EventSourceListener<String, String>() {
            @Override
            public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, String>> records) {
                results.putAll(records);
            }
        });

        // highWaterOffset method is called before this method returns, so we should be good!
        consumer.awaitHighWaterOffset(10, TimeUnit.SECONDS);

        Assert.assertEquals(0, results.size());
    }
}
