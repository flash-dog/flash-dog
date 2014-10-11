package org.log4mongo;

import static org.junit.Assert.*;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Steven.Zheng
 * @date 2013-1-18
 */
public class AsynMongoURILayoutAppenderTest {
	Logger logger1 = Logger.getLogger("org.log4mongo.contrib.JvmMonitor");
    Logger logger2 = Logger.getLogger("org.log4mongo.contrib.JvmMonitor2");


	@Test
	public final void test() throws InterruptedException {
		MDC.put("p1", 1);
		MDC.put("p2", "tttt");
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                    logger1.info("memoryUsed=121538k cpuUsed=0.0 threadCount=80");
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                    logger2.info("memoryUsed=121538k cpuUsed=0.0 threadCount=80");
            }
        };
        Thread t1=new Thread(runnable1);
        Thread t2=new Thread(runnable2);
        t1.setDaemon(false);
        t2.setDaemon(false);
        t1.start();
        Thread.sleep(1000);
        t2.start();

	}

    @Test
    public final void testMatch(){
        String a = "memoryUsed=121538k cpuUsed=0.0 threadCount=80";
        String matchstr = "^memoryUsed=(\\d+).*cpuUsed=(.*) threadCount=(\\d+)$";
        Assert.assertTrue(a.matches(matchstr));
        Pattern pattern = Pattern.compile(matchstr);
        Matcher matcher = pattern.matcher(a);
        if(matcher.find()) {
            for (int i =0;i<=matcher.groupCount();i++) {
                System.out.println(matcher.group(i));
            }
        }
    }

}
