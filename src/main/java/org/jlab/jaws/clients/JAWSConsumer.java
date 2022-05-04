package org.jlab.jaws.clients;

import org.jlab.jaws.eventsource.EventSourceConfig;
import org.jlab.jaws.eventsource.EventSourceTable;

import java.util.Properties;

/**
 * A JAWSConsumer extends an EventSourceTable with default property values for BOOTSTRAP_SERVERS and SCHEMA_REGISTRY
 * via the environment and also sets specific.avro.reader = true.
 */
public class JAWSConsumer extends EventSourceTable {
    /**
     * Create a new JAWSConsumer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public JAWSConsumer(Properties props) {
        super(setDefaults(props));
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        defaults.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, System.getenv("BOOTSTRAP_SERVERS"));
        defaults.put("schema.registry.url", System.getenv("SCHEMA_REGISTRY"));
        defaults.put("specific.avro.reader", "true");

        defaults.putAll(overrides);

        return defaults;
    }
}
