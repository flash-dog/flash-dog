package com.skymobi.monitor.model;

import java.util.Date;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-11-30 下午7:17
 * 度量因子的值
 */
public class MetricValue implements IdentifyObject{
    private double value;
    private Date createTime=new Date();
    private String name;
    private String content;
    private long  timeStamp=new Date().getTime();
    public MetricValue() {
    }

    public MetricValue(String name,double value) {
        this.name=name;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    
}
