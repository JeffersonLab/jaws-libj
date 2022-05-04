package org.jlab.jaws.eventsource;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * An EventSourceListener can be registered to receive batch-processing call-backs from
 * an EventSourceTable.
 *
 * @param <K> The type for message keys
 * @param <V> The type for message values
 */
public interface EventSourceListener<K, V> {
    /**
     * This callback is invoked only once, upon determination of initial state (high water mark reached).
     *
     * Note: The high water mark is queried upon connection to the Kafka server, but if producers submit messages
     * after that connection is established the client's knowledge of the high water mark will be out-of-date.
     */
    public default void highWaterOffset() {

    }

    /**
     * This callback indicates the high water mark was not reached before EVENT_SOURCE_HIGH_WATER_TIMEOUT.
     */
    public default void highWaterOffsetTimeout() {

    }

    /**
     * This callback is invoked periodically depending on EventSourceConfig.EVENT_SOURCE_POLL_MILLIS and
     * will contain no more than EventSourceConfig.EVENT_SOURCE_MAX_POLL_RECORDS.
     *
     * @param records The ordered list of records
     */
    public default void batch(List<EventSourceRecord<K, V>> records) {

    }
}
