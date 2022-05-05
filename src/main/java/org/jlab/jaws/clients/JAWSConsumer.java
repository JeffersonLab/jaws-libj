package org.jlab.jaws.clients;

import org.jlab.kafka.eventsource.EventSourceConfig;
import org.jlab.kafka.eventsource.EventSourceTable;

import java.util.Properties;

/**
 * A JAWSConsumer extends an EventSourceTable with default property values for BOOTSTRAP_SERVERS and SCHEMA_REGISTRY
 * via the environment and also sets specific.avro.reader = true.
 *
 * Type parameters:
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public class JAWSConsumer<K, V> extends EventSourceTable<K, V> {
    /**
     * Create a new JAWSConsumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public JAWSConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = JAWSClientDefault.setDefaults(overrides);
        defaults.put("specific.avro.reader", "true");

        defaults.putAll(overrides);

        return defaults;
    }
}
