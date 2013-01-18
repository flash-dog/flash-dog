package org.log4mongo;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.junit.Test;

/**
 * @author Steven.Zheng
 * @date 2013-1-18
 */
public class AsynMongoURILayoutAppenderTest {
	Logger logger = Logger.getLogger(AsynMongoURILayoutAppenderTest.class);

	@Test
	public final void test() throws InterruptedException {
		MDC.put("p1", 1);
		MDC.put("p2", "tttt");
		logger.info("testMCD");
		Thread.sleep(1000);
	}

}
