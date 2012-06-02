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
    TimeRange timeRange =new TimeRange();
    SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
    public void test_start() throws Exception {
        timeRange.setNow(DateUtils.parseDate("2011-11-11 00:00",new String[]{DATE_FORMAT}));
        timeRange.setLast(1);
        timeRange.setUnit(Calendar.HOUR);
        assertEquals("2011-11-10 23:00",sdf.format(timeRange.getStart()));
        timeRange.setUnit(Calendar.DATE);
        assertEquals("2011-11-10 00:00",sdf.format(timeRange.getStart()));
    }
}
