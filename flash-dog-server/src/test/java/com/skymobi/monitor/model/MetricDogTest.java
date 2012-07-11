/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.model;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.sql.Time;
import java.util.Date;

/**
 * @author Hill.Hu
 */
public class MetricDogTest extends TestCase {
    MetricDog dog;

    @Override
    public void setUp() throws Exception {
        dog = new MetricDog();
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
        Date now = Time.valueOf("09:00:00");
        assertTrue(now.after(Time.valueOf("00:00:00")));
        assertTrue(now.before(Time.valueOf("24:00:00")));
        dog.setStartTime("08:00:00");
        dog.setEndTime("10:00:00");
        assertTrue(dog.inWorkTime(now));
        dog.setExcludeTimeMode(true);
        assertFalse(dog.inWorkTime(now));
    }

    public void test_make_alert() throws Exception {
        assertEquals("", StringUtils.defaultIfEmpty(null,""));
    }
}
