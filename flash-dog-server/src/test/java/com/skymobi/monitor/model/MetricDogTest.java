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

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

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
        Date now = DateUtils.parseDate("09:00:00", new String[]{"HH:mm:ss"});

        dog.setStartTime("08:00:00");
        dog.setEndTime("10:00:00");
        assertTrue(dog.inWorkTime(now));
        dog.setExcludeTimeMode(true);
        assertFalse(dog.inWorkTime(now));

        now = DateUtils.parseDate("13:00:00", new String[]{"HH:mm:ss"});
        dog.setExcludeTimeMode(false);
        dog.setStartTime("08:00:00");
        dog.setEndTime("14:00:00");
        assertTrue(dog.inWorkTime(now));
        dog.setExcludeTimeMode(true);
        assertFalse(dog.inWorkTime(now));
    }

    public void test_make_alert() throws Exception {
        assertEquals("", StringUtils.defaultIfEmpty(null,""));
    }
}
