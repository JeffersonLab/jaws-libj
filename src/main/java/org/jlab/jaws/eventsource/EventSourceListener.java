package org.jlab.jaws.eventsource;

import java.util.LinkedHashMap;

/**
 * An EventSourceListener can be registered to receive batch-processing call-backs from
 * an EventSourceTable.  The first call-back is always the initial state of
 * the database (up to the high water mark).  Changes are reported in batches thereafter as a set of changes
 * (duplicate keys possible).
 *
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public interface EventSourceListener<K, V> {
    /**
     * This callback is invoked only once upon determination of initial state and contains the initial
     * set of unique records up to high water mark (latest keys overwrite older keys).
     *
     * @param records The initial set of unique records
     */
    public default void initialState(LinkedHashMap<K, EventSourceRecord<K, V>> records) {

    }

    /**
     * This callback is invoked periodically depending on EventSourceConfig.EVENT_SOURCE_POLL_MILLIS and
     * EventSourceConfig.EVENT_SOURCE_MAX_POLL_BEFORE_FLUSH settings with changes that occur after the initial state.
     *
     * @param records The set of changes
     */
    public default void changes(LinkedHashMap<K, EventSourceRecord<K, V>> records) {

    }
}
