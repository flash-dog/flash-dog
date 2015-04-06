package com.skymobi.monitor.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * a chart show one or more metrics
 *
 * @author hushan
 */
public class ChartView {
    private String title;
    private List<String> metricNames = Lists.newArrayList();

    public List<String> getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(List<String> metricNames) {
        this.metricNames = metricNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
