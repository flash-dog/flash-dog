package com.skymobi.monitor.mongodb;

import junit.framework.Assert;

import org.junit.Test;

import com.mongodb.MongoURI;

/**
 * @author Steven.Zheng
 * @date 2012-12-28
 */
public class MongoURITest {
	@Test
	public void testMongoURI(){
		MongoURI uri = new MongoURI("mongodb://172.16.3.82:27017,172.16.3.37:27017/monitor_test?slaveOk=true");
		Assert.assertTrue(uri.getOptions().slaveOk);
		
	}
}
