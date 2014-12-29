package com.skymobi.tomcat;

import org.apache.catalina.valves.AccessLogValve;
import org.apache.log4j.Logger;

import java.text.DateFormat;

/**
 * @author hill.hu
 */
public class Log4JAccessLogValve  extends AccessLogValve {
    private final Logger logger = Logger.getLogger(this.getClass());

    protected static final String info1 ="com.skymobi.tomcat.Log4JAccessLogValve";

    @Override
    public void log(String message) {
         logger.info(message);
    }

    @Override
    public String getInfo()
    {
        return info1;
    }

    @Override
    protected void open()
    {
    }
}
