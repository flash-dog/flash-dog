/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.model;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-11-23 上午9:09
 * 度量监控
 */
@SuppressWarnings("unused")
public class MetricDog {
    private static Logger logger = LoggerFactory.getLogger(MetricDog.class);

    private String name, desc;
    /**
     * 目标值
     */
    private double targetValue;
    private String operator;
    /**
     * 是否开启
     *
     * @see com.skymobi.monitor.model.MetricDog#inWorking()
     */
    private boolean enable;
    private String metricName;
    private boolean excludeTimeMode = false;
    private String startTime = "00:00:00";
    private String endTime = "24:00:00";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Alert work(Project project) {
        for (String metric : project.findMetricNames()) {
            if (StringUtils.equals(metric, metricName)) {
                MetricValue metricValue = project.findLastMetric(metricName);
                boolean fire = bite(metricValue.getValue());
                if (fire) {
                    Alert alert = new Alert();
                    alert.setTitle(String.format("【%s】->%s", project.getAlias(), name));

                    String _desc = StringUtils.defaultIfEmpty(desc, "");
                    String _content = StringUtils.defaultIfEmpty(metricValue.getContent(), "");

                    alert.setContent(String.format("%s:当前值=%s %s 阀值%s \n\n %s \n %s",
                            metricName, metricValue.getValue(), operator, targetValue, _desc, _content));
                    alert.setProjectName(project.getName());
                    return alert;
                }
            }
        }
        return null;
    }

    /**
     * 是否狂叫
     *
     * @param metricValue
     * @return
     */
    protected boolean bite(double metricValue) {
        if (StringUtils.equals("<", operator))
            return Double.compare(targetValue, metricValue) > 0;
        if (StringUtils.equals("=", operator))
            return Double.compare(targetValue, metricValue) == 0;
        if (StringUtils.equals(">", operator))
            return Double.compare(targetValue, metricValue) < 0;
        logger.warn("not support operator {} ,just support < , = , >", operator);
        return false;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        MetricDog.logger = logger;
    }

    public boolean getExcludeTimeMode() {
        return excludeTimeMode;
    }

    public void setExcludeTimeMode(boolean excludeTimeMode) {
        this.excludeTimeMode = excludeTimeMode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "MetricDog{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", targetValue=" + targetValue +
                ", operator='" + operator + '\'' +
                ", enable=" + enable +
                ", metricName='" + metricName + '\'' +
                ", excludeTimeMode=" + excludeTimeMode +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    /**
     * 是否正在工作
     *
     * @return
     */
    public boolean inWorking() {
        return enable && inWorkTime(new Date());

    }

    protected boolean inWorkTime(Date current) {
        Time now = Time.valueOf(sdf.format(current));
        Time start = Time.valueOf(startTime);
        Time end = Time.valueOf(endTime);
        //如果为"除了某时间段"模式
        if (excludeTimeMode) {
            return !(now.after(start) && now.before(end));
        } else {
            return now.after(start) && now.before(end);
        }

    }
}
