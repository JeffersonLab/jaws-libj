package org.jlab.jaws.clients;

import org.jlab.jaws.entity.AlarmLocation;
import org.junit.Test;

public class EntityTest {

    /**
     * Simply verifying the avro auto generate plugin actually generated the entities.
     */
    @Test
    public void autoGenereratedTest() {
        AlarmLocation location = new AlarmLocation("CEBAF");
    }
}
