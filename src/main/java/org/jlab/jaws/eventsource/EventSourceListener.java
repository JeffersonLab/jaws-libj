package org.jlab.jaws.eventsource;

import java.util.List;

/**
 * An EventSourceListener can be registered to receive batch-processing call-backs from
 * an EventSourceTable.  The first call-back is always the initial state of
 * the database (up to the high water mark).
 *
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public interface EventSourceListener<K, V> {
    public abstract void update(List<EventSourceRecord<K, V>> records);
}
