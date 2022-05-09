package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.jlab.jaws.entity.*;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
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

            String expected = "";

            try(CategoryProducer producer = new CategoryProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(CategoryProducer producer = new CategoryProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void classTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmClass>> results = new LinkedHashMap<>();

        try(ClassConsumer consumer = new ClassConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmClass>> records) {
                    results.putAll(records);
                }
            });

            AlarmClass expected = new AlarmClass("category",
                    AlarmPriority.P1_CRITICAL,
                    "rationale",
                    "correctiveaction",
                    "pointofcontactusername",
                    true,
                    true,
                    null,
                    null);

            try(ClassProducer producer = new ClassProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(ClassProducer producer = new ClassProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void instanceTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmInstance>> results = new LinkedHashMap<>();

        try(InstanceConsumer consumer = new InstanceConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmInstance>> records) {
                    results.putAll(records);
                }
            });

            AlarmInstance expected = new AlarmInstance("class",
                    new SimpleProducer(),
                    Arrays.asList(new String[]{"location1"}),
                    "maskedby",
                    "screencommand");

            try(InstanceProducer producer = new InstanceProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(InstanceProducer producer = new InstanceProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
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

            AlarmLocation expected = new AlarmLocation(null);

            try(LocationProducer producer = new LocationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(LocationProducer producer = new LocationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void overrideTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<OverriddenAlarmKey, EventSourceRecord<OverriddenAlarmKey, AlarmOverrideUnion>> results = new LinkedHashMap<>();

        try(OverrideConsumer consumer = new OverrideConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<OverriddenAlarmKey, EventSourceRecord<OverriddenAlarmKey, AlarmOverrideUnion>> records) {
                    results.putAll(records);
                }
            });

            AlarmOverrideUnion expected = new AlarmOverrideUnion(new LatchedOverride());

            try(OverrideProducer producer = new OverrideProducer(null)) {
                Future<RecordMetadata> future = producer.send(new OverriddenAlarmKey("TESTING",
                        OverriddenAlarmType.Latched), expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(OverrideProducer producer = new OverrideProducer(null)) {
                Future<RecordMetadata> future = producer.send(new OverriddenAlarmKey("TESTING",
                        OverriddenAlarmType.Latched), null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void activationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, AlarmActivationUnion>> results = new LinkedHashMap<>();

        try(ActivationConsumer consumer = new ActivationConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmActivationUnion>> records) {
                    results.putAll(records);
                }
            });

            AlarmActivationUnion expected = new AlarmActivationUnion(new SimpleAlarming());

            try(ActivationProducer producer = new ActivationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(ActivationProducer producer = new ActivationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void monologTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, IntermediateMonolog>> results = new LinkedHashMap<>();

        try(MonologConsumer consumer = new MonologConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String,
                        IntermediateMonolog>> records) {
                    results.putAll(records);
                }
            });

            IntermediateMonolog expected = new IntermediateMonolog(new EffectiveRegistration(),
                    new EffectiveActivation(null, new AlarmOverrideSet(), AlarmState.Normal),
                    new ProcessorTransitions());

            try(MonologProducer producer = new MonologProducer(null)) {
                Future<RecordMetadata> future = producer.send(MonologProducer.INTERMEDIATE_REGISTRATION_TOPIC,
                        "TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(MonologProducer producer = new MonologProducer(null)) {
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

        try(EffectiveRegistrationConsumer consumer = new EffectiveRegistrationConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveRegistration>> records) {
                    results.putAll(records);
                }
            });

            EffectiveRegistration expected = new EffectiveRegistration();

            try(EffectiveRegistrationProducer producer = new EffectiveRegistrationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveRegistrationProducer producer = new EffectiveRegistrationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void effectiveActivationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, EffectiveActivation>> results = new LinkedHashMap<>();

        try(EffectiveActivationConsumer consumer = new EffectiveActivationConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveActivation>> records) {
                    results.putAll(records);
                }
            });

            EffectiveActivation expected = new EffectiveActivation(null,
                    new AlarmOverrideSet(), AlarmState.Normal);

            try(EffectiveActivationProducer producer = new EffectiveActivationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveActivationProducer producer = new EffectiveActivationProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void effectiveAlarmTest() throws InterruptedException, ExecutionException, TimeoutException {
        LinkedHashMap<String, EventSourceRecord<String, EffectiveAlarm>> results = new LinkedHashMap<>();

        try(EffectiveAlarmConsumer consumer = new EffectiveAlarmConsumer(null)) {
            consumer.addListener(new EventSourceListener<>() {
                @Override
                public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveAlarm>> records) {
                    results.putAll(records);
                }
            });

            EffectiveAlarm expected = new EffectiveAlarm(new EffectiveRegistration(),
                    new EffectiveActivation(null, new AlarmOverrideSet(), AlarmState.Normal));

            try(EffectiveAlarmProducer producer = new EffectiveAlarmProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", expected);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }

            consumer.start();

            // highWaterOffset method is called before this method returns, so we should be good!
            consumer.awaitHighWaterOffset(2, TimeUnit.SECONDS);

            Assert.assertEquals(1, results.size());
            Assert.assertEquals(expected, results.values().iterator().next().getValue());
        } finally {
            // Cleanup
            try(EffectiveAlarmProducer producer = new EffectiveAlarmProducer(null)) {
                Future<RecordMetadata> future = producer.send("TESTING", null);

                // Block until sent or an exception is thrown
                future.get(2, TimeUnit.SECONDS);
            }
        }
    }
}
