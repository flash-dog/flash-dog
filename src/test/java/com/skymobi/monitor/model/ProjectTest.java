package com.skymobi.monitor.model;

import com.google.common.collect.Lists;
import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class ProjectTest extends TestCase {
    private static final String METRIC_COLLECTION = "test_metrics";
    Project project=new Project();

    @Override
    public void setUp() throws Exception {
        project.setMongoUri("mongodb://172.16.3.47/monitor_test");
        project.setName("test");
        project.setMetricCollection(METRIC_COLLECTION);


    }

    private void addMetric(String name, int value) {
        project.fetchMongoTemplate().save(new MetricValue(name,value),project.getMetricCollection());
    }

    @Override
    public void tearDown() throws Exception {
        project.fetchMongoTemplate().dropCollection(METRIC_COLLECTION);
    }

    public void test_find_metrics() throws Exception {
        addMetric("收入",10);
        addMetric("收入",10);
        assertEquals(1,project.findMetricNames().size());
        addMetric("总收入",12);
        assertEquals(2,project.findMetricNames().size());
        Thread.sleep(20);
        addMetric("收入",11);
        assertTrue(11==project.findLastMetric("收入").getValue());
    }

    public void test_has_member() throws Exception {
        project.setAdmins(Lists.newArrayList("hill.hu"));
        assertTrue(project.hasMember("hill.hu"));
        assertFalse(project.hasMember("gion.fang"));
    }
}
