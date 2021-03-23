package org.jlab.jaws.eventsource;

/**
 * A Record in the EventSourceTable.
 *
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public class EventSourceRecord<K,V> {
    private K key;
    private V value;

    public EventSourceRecord(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public String toString() {
        return (key == null ? "null" : key.toString()) + "=" + (value == null ? "null" : value.toString());
    }
}
