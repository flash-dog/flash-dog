package com.skymobi.monitor.util;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * springMVC+Jetty的容器
 *
 * @author Hill.Hu
 */
public class SpringJettyMain {

    private static final Logger logger = LoggerFactory.getLogger(SpringJettyMain.class);

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/springWeb-start.xml");
        WebAppContext webAppContext = applicationContext.getBean("webAppContext", WebAppContext.class);
        logger.info("start jetty web context context= " + webAppContext.getContextPath() + ";resource base=" + webAppContext.getResourceBase());

        logger.info("start jetty web context context= " + webAppContext.getContextPath() + ";descriptor=" + webAppContext.getDescriptor());
        try {
            Server server = applicationContext.getBean("jettyServer", Server.class);
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start jetty server on " + ":" + ", cause: " + e.getMessage(), e);
        }
    }


}