package org.jlab.jaws.clients;

import org.jlab.kafka.eventsource.EventSourceConfig;
import org.jlab.kafka.eventsource.EventSourceTable;

import java.util.Properties;

/**
 * A JAWSClientDefault provides default property values for BOOTSTRAP_SERVERS and SCHEMA_REGISTRY
 * via the environment and the sensible default of localhost otherwise.
 */
public class JAWSClientDefault {
    /**
     * Sets defaults for BOOTSTRAP_SERVERS and SCHEMA_REGISTRY, then applies overrides.
     *
     * @param overrides The overrides.
     * @return The properties, contain defaults set by this method unless overridden
     */
    public static Properties setDefaults(Properties overrides) {
        Properties defaults = new Properties();

        String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");

        if(BOOTSTRAP_SERVERS == null) {
            BOOTSTRAP_SERVERS = "localhost:9092";
        }

        String SCHEMA_REGISTRY = System.getenv("SCHEMA_REGISTRY");

        if(SCHEMA_REGISTRY == null) {
            SCHEMA_REGISTRY = "http://localhost:8081";
        }

        defaults.put(EventSourceConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        defaults.put("schema.registry.url", SCHEMA_REGISTRY);

        defaults.putAll(overrides);

        return defaults;
    }
}
