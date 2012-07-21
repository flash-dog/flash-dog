package com.skymobi.monitor.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 复合视图
 * @author Steven.Zheng
 * @date 2012-7-21
 */
public class ComplexView {
	private String title;
	private List<String> metricNames = Lists.newArrayList();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getMetricNames() {
		return metricNames;
	}
	public void setMetricNames(List<String> metricNames) {
		this.metricNames = metricNames;
	}
	
}
