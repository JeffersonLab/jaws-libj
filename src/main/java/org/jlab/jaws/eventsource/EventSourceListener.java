package org.jlab.jaws.eventsource;

import java.util.LinkedHashMap;

/**
 * An EventSourceListener can be registered to receive batch-processing call-backs from
 * an EventSourceTable.  The batch callback reports records in batches.  The highWaterOffset callback reports
 * when the last offset is reached (the high water mark).
 *
 * In the batch call-back newer keys replace older keys so intermediate states
 * are not reported.  The call-back provides an ordered set of unique records.
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
     * This callback indicates the high water mark was not reached before EVENT_SOURCE_HIGH_WATER_TIMEOUT,
     * which is generally due to an error condition such as delete topic policy instead of compact.
     */
    public default void highWaterOffsetTimeout() {

    }

    /**
     * This callback is invoked periodically depending on EventSourceConfig.EVENT_SOURCE_POLL_MILLIS and
     * will contain no more than EventSourceConfig.EVENT_SOURCE_MAX_POLL_RECORDS.
     *
     * @param records The ordered set of unique changes
     */
    public default void batch(LinkedHashMap<K, EventSourceRecord<K, V>> records) {

    }
}
