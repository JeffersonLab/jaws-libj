package org.jlab.jaws.eventsource;

import java.util.List;

public interface EventSourceListener<K, V> {
    public abstract void update(List<EventSourceRecord<K, V>> changes);
}
