package org.jlab.jaws.eventsource;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSourceRecord<?, ?> that = (EventSourceRecord<?, ?>) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
