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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

/**
 * 项目,包括4个方面
 * 1.图表
 * 2.告警
 * 3.任务
 * 4.度量因子
 *
 * @author hill.hu
 */
@SuppressWarnings("unchecked")
@Document
public class Project {
    private static Logger logger = LoggerFactory.getLogger(Project.class);

    private final static Map<String, Mongo> MONGO_MAP = Maps.newHashMap();
    @Id
    private String name;
    private String alias;
    private String desc;

    /**
     * mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
     */
    private String mongoUri;
    @Deprecated
    private List<Chart> charts = Lists.newArrayList();
    private List<Task> tasks = Lists.newArrayList();

    private List<MetricDog> metricDogs = Lists.newArrayList();
    private List<String> admins = Lists.newArrayList();
    private String metricCollection;
    private String logCollection;
    private TimeRange timeRange = TimeRange.lastDay();
    private String mailList;
    private Properties properties = new Properties();
    /**
     * 用于存储视图
     * @see  #chartViews
     */
    @Deprecated
    private Map<String, String> views = new HashMap();
    /**
     * 图表视图
     */
    private List<ChartView> chartViews=Lists.newArrayList();
    private Status status=Status.FINE;
    private Date createTime=new Date();

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

    public List<ChartView> getChartViews() {
        return chartViews;
    }

    public void setChartViews(List<ChartView> chartViews) {
        this.chartViews = chartViews;
    }

    public String getAlias() {
        if (alias == null)
            alias = name;
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getDesc() {
        return desc;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }


    public List<Task> getTasks() {
        return tasks;
    }


    public void setTasks(List tasks) {
        this.tasks = tasks;
    }


    public Task findTask(String taskName) {
        for (Task task : tasks) {
            if (taskName.equals(task.getName()))
                return task;
        }
        return null;
    }


    public void saveTask(Task task) {
        Task oldTask = findTask(task.getName());
        if (oldTask == null) {
            tasks.add(task);
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                if (StringUtils.equals(tasks.get(i).getName(), task.getName())) {
                    tasks.set(i, task);
                }
            }
        }


    }


    public Task removeTask(String taskName) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (StringUtils.equals(task.getName(), taskName)) {
                tasks.remove(i);
                return task;
            }
        }
        return null;
    }


    public Chart findChart(String chartName) {
        for (Chart chart : charts) {
            if (StringUtils.equals(chart.getName(), chartName))
                return chart;
        }
        return null;
    }


    public void saveChart(Chart chart) {
        this.charts.remove(chart);
        this.charts.add(chart);
    }

    public List getCharts() {
        return charts;
    }

    public void setCharts(List charts) {
        this.charts = charts;
    }


    public List<MetricDog> getMetricDogs() {
        return metricDogs;
    }

    public void setMetricDogs(List<MetricDog> metricDogs) {
        this.metricDogs = metricDogs;
    }

    public Map<String, String> getViews() {
        return views;
    }

    public void setViews(Map<String, String> views) {
        this.views = views;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public String getMetricCollection() {
        if (metricCollection == null)
            metricCollection = name + "_metrics";
        return metricCollection;
    }

    public void setMetricCollection(String metricCollection) {
        this.metricCollection = metricCollection;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

    public MongoTemplate fetchMongoTemplate() {

        try {
            Mongo mongo;
            if (MONGO_MAP.containsKey(mongoUri)) {
                mongo = MONGO_MAP.get(mongoUri);

            } else {
                mongo = new Mongo(new MongoURI(mongoUri));
                MONGO_MAP.put(mongoUri, mongo);

            }

            MongoURI uri = new MongoURI(mongoUri);
            return new MongoTemplate(new SimpleMongoDbFactory(mongo, uri.getDatabase(),
                    new UserCredentials(uri.getUsername(), parseChars(uri.getPassword()))));

        } catch (Exception e) {
            logger.error("mongo db error ,uri={}", mongoUri, e);
            return null;
        }

    }
    
	private static String parseChars(char[] chars) {
		return chars == null ? null : String.valueOf(chars);
	}

    public List<String> findMetricNames() {

        try {
            List list = fetchMongoTemplate().getCollection(metricCollection).distinct("name");
            logger.debug("project [{}] has  metrics  ={} ,mongo={}", new Object[]{name, list, mongoUri});
            return list;
        } catch (Exception e) {
            logger.error("load metrics fail projectName=" + name, e);
        }
        return Lists.newArrayList();
    }

    private Query fetchTimeQuery() {
        return new Query(Criteria.where(Constants.TIME_STAMP_FIELD_NAME).gt(timeRange.getStart().getTime()));

    }


    public List<MetricValue> findMetricData(String metricName) {

        Query query = fetchTimeQuery();
        query.addCriteria(Criteria.where("name").is(metricName));
        query.sort().on(Constants.TIME_STAMP_FIELD_NAME, Order.ASCENDING);
        logger.debug("find metric value by {} ,mongo={}", query.getQueryObject(), mongoUri);
        return fetchMongoTemplate().find(query, MetricValue.class, metricCollection);
    }

    /**
     * 返回最新值
     *
     * @param metricName
     * @return
     */
    public MetricValue findLastMetric(String metricName) {
        Query query = BasicQuery.query(Criteria.where("name").is(metricName));
        query.sort().on(Constants.TIME_STAMP_FIELD_NAME, Order.DESCENDING);
        return fetchMongoTemplate().findOne(query, MetricValue.class, metricCollection);
    }


    public void saveDog(MetricDog metricDog) {
        removeDog(metricDog.getName());
        metricDogs.add(metricDog);
    }

    public MetricDog findDog(String dogName) {
        return (MetricDog) CollectionUtils.find(metricDogs, new BeanPropertyValueEqualsPredicate("name", dogName));

    }


    public void removeDog(String dogName) {
        for(int i=0;i<metricDogs.size();i++){
            if(metricDogs.get(i).getName().equals(dogName))  {
                logger.debug("delete dog [{}] from [{}]",dogName,name);
                metricDogs.remove(i);
                return;
            }
        }

       logger.warn("delete fail,can't find dog [{}] from [{}]",dogName,name);


    }

    public String getMailList() {
        return mailList;
    }

    public void setMailList(String mailList) {
        this.mailList = mailList;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                ", mongoUri='" + mongoUri + '\'' +
                ", metricDogs=" + metricDogs +
                ", metricCollection='" + metricCollection + '\'' +
                ", timeRange=" + timeRange +
                ", mailList='" + mailList + '\'' +
                ", taskCount='" + tasks.size() + '\'' +
                '}';
    }

    public boolean hasMember(String userName) {
        return admins != null && admins.contains(userName);
    }

    public String getLogCollection() {
        return logCollection;
    }

    public void setLogCollection(String logCollection) {
        this.logCollection = logCollection;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
