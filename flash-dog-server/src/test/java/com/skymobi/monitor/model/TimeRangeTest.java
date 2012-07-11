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
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-6 上午9:14
 */
public class TimeRangeTest extends TestCase {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    TimeRange timeRange = new TimeRange();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public void test_start() throws Exception {
        timeRange.setNow(DateUtils.parseDate("2011-11-11 00:00", new String[]{DATE_FORMAT}));
        timeRange.setLast(1);
        timeRange.setUnit(Calendar.HOUR);
        assertEquals("2011-11-10 23:00", sdf.format(timeRange.getStart()));
        timeRange.setUnit(Calendar.DATE);
        assertEquals("2011-11-10 00:00", sdf.format(timeRange.getStart()));
    }
}
