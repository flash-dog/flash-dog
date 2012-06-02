package com.skymobi.monitor.service;

import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class LogsServiceTest extends TestCase {
    LogsService logsService;
    public void test_find_last() throws Exception {
        String json = "{'message':{ '$exists' : true },'timestamp':{$gt:new Date(0)}}";

    }
}
