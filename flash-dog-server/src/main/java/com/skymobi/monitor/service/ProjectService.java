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
package com.skymobi.monitor.service;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.ChartView;
import com.skymobi.monitor.model.Constants;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hill.hu
 */
@SuppressWarnings("unchecked")
public class ProjectService {
    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Resource
    private AlertService alertService;
    @Resource
    private MongoTemplate mongoTemplate;
    private String collectionName;
    @Resource
    private TaskService taskService;


    public List<Project> findProjects() {
        Query query = new Query();
        query.sort().on("name", Order.ASCENDING);
        List<Project> projects = mongoTemplate.
                find(query, Project.class, collectionName);
        return projects;
    }

    public void saveProject(Project project) {

        logger.debug("project has be save {}", project);
        mongoTemplate.save(project, collectionName);

    }

    public Project findProject(String projectName) {
        Project project = mongoTemplate.findOne(new Query(Criteria.where("name").is(projectName)),
                Project.class, collectionName);

        if(project!=null)
            checkChartView(project);
        return project;
    }

    /**
     * 从老版本的view兼容新的chartView
     * @param project
     */
    private void checkChartView(Project project) {
        List<ChartView> chartViews = project.getChartViews();
        Map<String, String> views = project.getViews();

        if (chartViews.isEmpty() && !views.isEmpty()) {
            for (String metricName : views.keySet()) {
                ChartView view=new ChartView();
                view.setTitle(metricName);
                view.setMetricNames(Lists.newArrayList(views.get(metricName).split(",")));
                chartViews.add(view);
            }

        }
        if (chartViews.isEmpty()) {
            for (String metricName : project.findMetricNames())  {
                ChartView view=new ChartView();
                view.setTitle(metricName);
                view.setMetricNames(Lists.newArrayList(metricName));
                chartViews.add(view);
            }
        }
    }

    public void init() {
        List<Project> projects = findProjects();
        for (Project project : projects) {
            taskService.startTasks(project);
        }

    }


    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Project saveTask(String projectName, Task task) {
        Project project = findProject(projectName);
        project.saveTask(task);
        taskService.scheduledTask(project, task);
        saveProject(project);

        return project;
    }

    public void removeTask(String projectName, String taskName) {
        Project project = findProject(projectName);
        Task task = project.removeTask(taskName);
        saveProject(project);
        taskService.removeScheduled(projectName, task);
    }


    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void remove(String projectName) {
        Project project = findProject(projectName);
        if (project == null) {
            logger.warn(" project  [{}] not exist", projectName);
            return;
        }
        for (Task task : project.getTasks()) {
            removeTask(projectName, task.getName());
        }
        mongoTemplate.remove(project,
                collectionName);
        logger.debug("remove project by name={} success", projectName);
    }

    public void create(Project project) throws IllegalArgumentException {
        Assert.isNull(findProject(project.getName()), "project  [" + project.getName() + "] has exist");
        MongoTemplate template = project.fetchMongoTemplate();
        Assert.notNull(template, "mongo uri is not access");
        Assert.notNull(template.getDb(), "mongo uri is not access");

        saveProject(project);
    }
}
