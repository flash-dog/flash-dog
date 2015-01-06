package org.flashdog.agent;

import com.mongodb.DBObject;
import junit.framework.Assert;
import junit.framework.TestCase;

public class LogFileTailerListenerTest extends TestCase {
    LogFileTailerListener listener = new LogFileTailerListener();
    String line;

    @Override
    public void setUp() throws Exception {
        listener.setPatternTxt(".*\\[(.*)\\] - (.*)");
        listener.setFields("className message");
        line = "2015-01-05 20:32:30,985 INFO [org.springframework.beans.factory.xml.XmlBeanDefinitionReader] - Loading XML bean definitions from class path resource [spring/spring-security.xml]";

    }

    public void test_congert() throws Exception {
        DBObject dbObject = listener.convert(line);
        Assert.assertEquals("Loading XML bean definitions from class path resource [spring/spring-security.xml]",dbObject.get("message"));
        Assert.assertEquals("org.springframework.beans.factory.xml.XmlBeanDefinitionReader",dbObject.get("className"));

    }

    public void test_hand() throws Exception {
        listener.handle(line);

    }
}