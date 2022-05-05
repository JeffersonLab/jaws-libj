package org.jlab.jaws.clients;

import org.jlab.kafka.eventsource.EventSourceConfig;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class ClientTest {

    @Test
    public void categoryTest() throws InterruptedException {
        Properties props = new Properties();
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, "kafka:9092");
        props.put("schema.registry.url", "http://registry:8081");

        /*CategoryConsumer consumer = new CategoryConsumer(props);

        LinkedHashMap<String, EventSourceRecord<String, String>> results = new LinkedHashMap<>();

        consumer.addListener(new EventSourceListener<String, String>() {
            @Override
            public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, String>> records) {
                results.putAll(records);
            }
        });

        // highWaterOffset method is called before this method returns, so we should be good!
        consumer.awaitHighWaterOffset(10, TimeUnit.SECONDS);

        Assert.assertEquals(1, results.size());*/
    }
}
