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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 * @author hill.hu
 *         <p/>
 *         度量监控
 */
public class MetricDog {
    private static Logger logger = LoggerFactory.getLogger(MetricDog.class);
    private final static ConcurrentMap<String, Object> hasFireMetrics = new MapMaker().expiration(1, TimeUnit.HOURS).makeMap();
    private final static ConcurrentMap<String, AtomicInteger> metricFireTimes = new MapMaker().expiration(6, TimeUnit.HOURS).makeMap();
    public static final String LEVEL_ERROR = "ERROR";
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
    //通知人邮件列表
    private String mailList;
    /**
     * 告警级别,WARN,ERROR
     */
    private String level = "WARN";
    private int times = 2;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

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
    
    public String getMailList() {
        return mailList;
    }

    public void setMailList(String mailList) {
        this.mailList = mailList;
    }

    public List<Alert> work(Project project) {
        List<Alert> alerts = Lists.newArrayList();
        for (String metric : project.findMetricNames()) {
            if (StringUtils.equals(metric, metricName)) {
                MetricValue metricValue = project.findLastMetric(metricName);
                String cacheKey = project.getName() + "_" + this.getName() + "_" + metricName + metricValue.getTimeStamp();
                logger.debug("current value={} ,dog={}", metricValue.getValue(), this);
                //这个值已经处理过了，忽略
                if (hasFireMetrics.containsKey(cacheKey)) {
                    logger.debug("this value has fire,just ignore {}", cacheKey);
                    continue;
                } else {
                    hasFireMetrics.put(cacheKey, true);
                }
                boolean fire = bite(metricValue.getValue());
                if (fire) {
                    Alert alert = new Alert();
                    alert.setTitle(String.format("【%s】->%s", project.getAlias(), name));

                    String _desc = StringUtils.defaultIfEmpty(desc, "");
                    String _content = StringUtils.defaultIfEmpty(metricValue.getContent(), "");
                    alert.setIp(metricValue.getIp() != null ? metricValue.getIp() : "127.0.0.1");
                    alert.setContent(String.format("%s:当前值=%s %s 阀值%s \n\n %s \n %s",
                            metricName, metricValue.getValue(), operator, targetValue, _desc, _content));
                    alert.setProjectName(project.getName());
                    alert.setMetricDog(this);
                    String _level = fixLevel(project, alert);
                    alert.setLevel(_level);
                    alerts.add(alert);

                } else {
                    //重新置0
                    resetFireTimes(project.getName(), metricName);
                }
            }
        }
        return alerts;
    }

    private String fixLevel(Project project, Alert alert) {
        String _level = level;
        int currentTime = incrementFireTimes(project.getName(), metricName);
        if (!LEVEL_ERROR.equals(level) && currentTime >= times) {
            _level = LEVEL_ERROR;
            logger.info("连续告警次数达到{}次，升级到[错误],alert={}", times, alert);
        }
        return _level;
    }

    private int incrementFireTimes(String projectName, String metricName) {
        String metricNotifyKey = projectName + "_" + metricName;
        metricFireTimes.putIfAbsent(metricNotifyKey, new AtomicInteger(0));
        return metricFireTimes.get(metricNotifyKey).incrementAndGet();
    }

    private void resetFireTimes(String projectName, String metricName) {
        String metricNotifyKey = projectName + "_" + metricName;
        metricFireTimes.put(metricNotifyKey, new AtomicInteger(0));
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
                ", level='" + level + '\'' +
                '}';
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /**
     * 是否正在工作
     *
     * @return
     */
    public boolean inWorking() {
        return enable && inWorkTime(new Date());

    }

    protected boolean inWorkTime(Date current) {
        Date now = null;
        try {
            now = sdf.parse(sdf.format(current));

            Date start = sdf.parse(startTime) ;
            Date end = sdf.parse(endTime );
            //如果为"除了某时间段"模式
            if (excludeTimeMode) {
                return !(now.after(start) && now.before(end));
            } else {
                return now.after(start) && now.before(end);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
