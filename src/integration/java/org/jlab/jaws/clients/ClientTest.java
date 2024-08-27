package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.jlab.jaws.entity.*;
import org.jlab.kafka.eventsource.EventSourceConfig;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientTest {

    private static final int HIGHWATER_TIMEOUT_SECONDS = 5;

    private Properties clientOverrides = new Properties();

    public static String getBootstrapServers() {
        String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");

        if(bootstrapServers == null) {
            bootstrapServers = "localhost:9094";
        }

        return bootstrapServers;
    }

    @Before
    public void setup(){
        clientOverrides.setProperty(EventSourceConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        clientOverrides.setProperty("auto.register.schemas", "false");
        clientOverrides.setProperty("use.latest.version", "true");
    }


    @Test
    public void systemTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmSystem>> results = new LinkedHashMap<>();

        try(SystemConsumer consumer = new SystemConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmSystem>> records) {
                    results.putAll(records);
                }
            });

            AlarmSystem expected = new AlarmSystem("team1");

            try(SystemProducer producer = new SystemProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(SystemProducer producer = new SystemProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void actionTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmAction>> results = new LinkedHashMap<>();

        try(ActionConsumer consumer = new ActionConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmAction>> records) {
                    results.putAll(records);
                }
            });

            AlarmAction expected = new AlarmAction("system",
                    AlarmPriority.P1_CRITICAL,
                    "rationale",
                    "correctiveaction",
                    true,
                    true,
                    null,
                    null);

            try(ActionProducer producer = new ActionProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(ActionProducer producer = new ActionProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void alarmTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, Alarm>> results = new LinkedHashMap<>();

        try(AlarmConsumer consumer = new AlarmConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, Alarm>> records) {
                    results.putAll(records);
                }
            });

            Alarm expected = new Alarm("action", "device",
                    new Source(),
                    Arrays.asList(new String[]{"location1"}),
                    "managedby",
                    "maskedby",
                    "screencommand");

            try(AlarmProducer producer = new AlarmProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(AlarmProducer producer = new AlarmProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void locationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> results = new LinkedHashMap<>();

        try(LocationConsumer consumer = new LocationConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> records) {
                    results.putAll(records);
                }
            });

            AlarmLocation expected = new AlarmLocation(null);

            try(LocationProducer producer = new LocationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(LocationProducer producer = new LocationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void overrideTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<AlarmOverrideKey, EventSourceRecord<AlarmOverrideKey, AlarmOverrideUnion>> results = new LinkedHashMap<>();

        try(OverrideConsumer consumer = new OverrideConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<AlarmOverrideKey, EventSourceRecord<AlarmOverrideKey, AlarmOverrideUnion>> records) {
                    results.putAll(records);
                }
            });

            AlarmOverrideUnion expected = new AlarmOverrideUnion(new LatchedOverride());

            try(OverrideProducer producer = new OverrideProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send(new AlarmOverrideKey("TESTING",
                        OverriddenAlarmType.Latched), expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(OverrideProducer producer = new OverrideProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send(new AlarmOverrideKey("TESTING",
                        OverriddenAlarmType.Latched), null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void activationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmActivationUnion>> results = new LinkedHashMap<>();

        try(ActivationConsumer consumer = new ActivationConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmActivationUnion>> records) {
                    results.putAll(records);
                }
            });

            AlarmActivationUnion expected = new AlarmActivationUnion(new Activation());

            try(ActivationProducer producer = new ActivationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(ActivationProducer producer = new ActivationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void monologTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, IntermediateMonolog>> results = new LinkedHashMap<>();

        try(MonologConsumer consumer = new MonologConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String,
                        IntermediateMonolog>> records) {
                    results.putAll(records);
                }
            });

            IntermediateMonolog expected = new IntermediateMonolog(new EffectiveRegistration(),
                    new EffectiveNotification(null, new AlarmOverrideSet(), AlarmState.Normal),
                    new ProcessorTransitions());

            try(MonologProducer producer = new MonologProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send(MonologProducer.INTERMEDIATE_REGISTRATION_TOPIC,
                        "TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(MonologProducer producer = new MonologProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send(MonologProducer.INTERMEDIATE_REGISTRATION_TOPIC,
                        "TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void effectiveRegistrationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, EffectiveRegistration>> results = new LinkedHashMap<>();

        try(EffectiveRegistrationConsumer consumer = new EffectiveRegistrationConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveRegistration>> records) {
                    results.putAll(records);
                }
            });

            EffectiveRegistration expected = new EffectiveRegistration();

            try(EffectiveRegistrationProducer producer = new EffectiveRegistrationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveRegistrationProducer producer = new EffectiveRegistrationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void effectiveActivationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, EffectiveNotification>> results = new LinkedHashMap<>();

        try(EffectiveNotificationConsumer consumer = new EffectiveNotificationConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveNotification>> records) {
                    results.putAll(records);
                }
            });

            EffectiveNotification expected = new EffectiveNotification(null,
                    new AlarmOverrideSet(), AlarmState.Normal);

            try(EffectiveNotificationProducer producer = new EffectiveNotificationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveNotificationProducer producer = new EffectiveNotificationProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void effectiveAlarmTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, EffectiveAlarm>> results = new LinkedHashMap<>();

        try(EffectiveAlarmConsumer consumer = new EffectiveAlarmConsumer(clientOverrides)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveAlarm>> records) {
                    results.putAll(records);
                }
            });

            EffectiveAlarm expected = new EffectiveAlarm(new EffectiveRegistration(),
                    new EffectiveNotification(null, new AlarmOverrideSet(), AlarmState.Normal));

            try(EffectiveAlarmProducer producer = new EffectiveAlarmProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            boolean reached = consumer.awaitHighWaterOffset(HIGHWATER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if(!reached) {
                throw new TimeoutException("Took too long to reach high water");
            }

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveAlarmProducer producer = new EffectiveAlarmProducer(clientOverrides)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }
}
