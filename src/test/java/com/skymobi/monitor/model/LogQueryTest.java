package com.skymobi.monitor.model;

import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class LogQueryTest extends TestCase {
    LogQuery query;
    @Override
    public void setUp() throws Exception {
         query=new LogQuery();
    }

    public void test_query() throws Exception {

        assertEquals("{ }",query.toQuery().toString());
        query.setStart("2012-02-03 10:00:00");
        
        assertEquals("{ \"timestamp\" : { \"$gt\" : { \"$date\" : \"2012-02-03T02:00:00.000Z\"}}}",query.toQuery().toString());
        query.setEnd("2012-02-03 11:00:00");
        query.setLevel("ERROR");
        query.setKeyWord("hello");
        System.out.println(query.toQuery().toString());
//        assertEquals("{ \"timestamp\" : { \"$gt\" : { \"$date\" : \"1970-01-01T00:01:40.000Z\"}} , \"$where\" : \"this.message && this.message.match('hello')\"}",query.toQuery().toString());
    }
    

}
