package com.skymobi.monitor.model;

import junit.framework.TestCase;

import java.sql.Time;
import java.util.Date;

/**
 * @author Hill.Hu
 */
public class MetricDogTest extends TestCase {
    MetricDog dog  ;

    @Override
    public void setUp() throws Exception {
        dog =new MetricDog();
    }

    public void test_watch() throws Exception {
        dog.setTargetValue(10);
        dog.setOperator(">");
        assertFalse(dog.bite(9));
        assertTrue(dog.bite(11));

        dog.setTargetValue(10);
        dog.setOperator("=");
        assertFalse(dog.bite(9));
        assertFalse(dog.bite(11));
        assertTrue(dog.bite(10));

        dog.setTargetValue(10);
        dog.setOperator("<");
        assertTrue(dog.bite(9));
        assertFalse(dog.bite(11));
        assertFalse(dog.bite(10));
    }

    public void test_is_work() throws Exception {
        Date now=Time.valueOf("09:00:00");
        assertTrue(now.after(Time.valueOf("00:00:00")));
        assertTrue(now.before(Time.valueOf("24:00:00")));
        dog.setStartTime("08:00:00");
        dog.setEndTime("10:00:00");
        assertTrue(dog.inWorkTime(now));
        dog.setExcludeTimeMode(true);
        assertFalse(dog.inWorkTime(now));
    }
}
