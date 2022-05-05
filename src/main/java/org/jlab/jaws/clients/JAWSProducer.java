package org.jlab.jaws.clients;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Header;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

/**
 * A JAWSProducer extends KafkaProducer and provides default property values for BOOTSTRAP_SERVERS and SCHEMA_REGISTRY
 * via the environment.
 *
 * Type parameters:
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public class JAWSProducer<K, V> extends KafkaProducer<K, V> {
    private Properties props;

    /**
     * Create a new JAWSProducer with the provided property overrides.
     *
     * @param props The properties, which will override any defaults set by this class
     */
    public JAWSProducer(Properties props) {
        super(setDefaults(props));

        // We hang onto a reference so we can pull out ProducerConfig.CLIENT_ID_CONFIG
        this.props = props;
    }

    private static Properties setDefaults(Properties overrides) {
        Properties defaults = JAWSClientDefault.setDefaults(overrides);

        defaults.putAll(overrides);

        return defaults;
    }

    /**
     * Attempt to determine the hostname.  First COMPUTERNAME environment variable is checked, next HOSTNAME environment
     * variable, and finally a InetAddress.getLocalHost().getHostName() DNS lookup is performed.
     *
     * @return The hostname, or null if unable to determine
     */
    public static String getHostname() {
        String host = System.getenv("COMPUTERNAME");

        if(host == null) {
            host = System.getenv("HOSTNAME");
        }

        if(host == null) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch(UnknownHostException e) {
                // Ignore, oh well, we tried.
            }
        }

        return host;
    }

    /**
     * Attempt to build default JAWS Headers, which contain user, producer, and host names.  The user name is pulled
     * from System.getProperty("user.name").  The producer name is pulled from the config
     * ProducerConfig.CLIENT_ID_CONFIG.  The host name comes from JAWSProducer.getHostname().
     *
     * @return The default headers
     */
    public Iterable<Header> getDefaultHeaders() {

        ArrayList<Header> headers = new ArrayList<>();

        headers.add(new Header() {
            @Override
            public String key() {
                return "user";
            }

            @Override
            public byte[] value() {
                String user = System.getProperty("user.name");
                if(user == null) {
                    user = "";
                }
                return user.getBytes(StandardCharsets.UTF_8);
            }
        });

        headers.add(new Header() {
            @Override
            public String key() {
                return "producer";
            }

            @Override
            public byte[] value() {
                String producer = props.getProperty(ProducerConfig.CLIENT_ID_CONFIG);
                if(producer == null) {
                    producer = "";
                }

                return producer.getBytes(StandardCharsets.UTF_8);
            }
        });

        headers.add(new Header() {
            @Override
            public String key() {
                return "host";
            }

            @Override
            public byte[] value() {
                String host = getHostname();
                if(host == null) {
                    host = "";
                }
                return host.getBytes(StandardCharsets.UTF_8);
            }
        });

        return headers;
    }
}
