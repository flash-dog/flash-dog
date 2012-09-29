package com.skymobi.monitor.util;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.MetricValue;
import junit.framework.TestCase;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Hill.Hu
 */
@SuppressWarnings("unchecked")
public class ChartUtilTest   {


    @Test
    public void test_format() throws Exception {

        String[] patten=new String[]{"M"};
        long m7 = DateUtils.parseDate("7", patten).getTime();
        long m8 = DateUtils.parseDate("8", patten).getTime();
        long m9 = DateUtils.parseDate("9", patten).getTime();
        List<MetricValue> metricValues1= Lists.newArrayList(new MetricValue("pay",2, m8) );
        List<MetricValue> metricValues2= Lists.newArrayList(new MetricValue("request",10,m7),new MetricValue("request",12,m8),new MetricValue("request",13,m9) );
        List<MetricValue> metricValues3= Lists.newArrayList();
        List<List<MetricValue>> lists = Lists.newArrayList(metricValues1, metricValues2,metricValues3);
        List<List> chartRows = ChartUtil.format(lists);
         Assert.assertEquals(4,chartRows.size());
        //time ，pay，request
        Assert.assertEquals("[[time, pay, request]," +
                " [7月01 00:00, 0.0, 10.0], " +
                "[8月01 00:00, 2.0, 12.0], " +
                "[9月01 00:00, 2.0, 13.0]]",
                chartRows.toString());

    }

    @Test
    public void test_for_empty() throws Exception {
        List<List<MetricValue>> empty=Lists.newArrayList();
        List<List> chartRows = ChartUtil.format(empty);
        Assert.assertTrue(chartRows.isEmpty());
    }
}
