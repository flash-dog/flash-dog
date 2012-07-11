package com.skymobi.monitor.model;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * author: Hill.Hu
 */
public class LogTest {
    @Test
    public void test_to_str() throws Exception {
        Log log=new Log();
        log.setClassName(log.getClass().getName());

        log.setLevel("INFO");
        log.setMessage("buy a ticket for <Bolt>");
        log.setTimestamp(DateUtils.parseDate("2012-07-08 12:00:11",new String[]{"yyyy-MM-dd hh:mm:ss"}));
        Assert.assertEquals("2012-07-08 00:00:11 INFO [com.skymobi.monitor.model.Log] - buy a ticket for <Bolt>", log.toString());

        log.setIp("172.16.3.16");
        log.setPid("7721@root");
        Assert.assertEquals("2012-07-08 00:00:11 7721@root 172.16.3.16 INFO [com.skymobi.monitor.model.Log] - buy a ticket for <Bolt>", log.toString());

    }
}
