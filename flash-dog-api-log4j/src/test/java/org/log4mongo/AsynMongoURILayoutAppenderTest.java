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
	Logger logger = Logger.getLogger("org.log4mongo.contrib.JvmMonitor");

	@Test
	public final void test() throws InterruptedException {
		MDC.put("p1", 1);
		MDC.put("p2", "tttt");
//        while(true) {
            logger.info("memoryUsed=121538k cpuUsed=0.0 threadCount=80");
//        }
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
