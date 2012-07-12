package com.skymobi.monitor.util;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import com.skymobi.monitor.service.ProjectService;
import org.apache.log4j.Logger;
import org.log4mongo.MongoDbAppender;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Enumeration;
import java.util.List;

/**
 * author: Hill.Hu
 */
@SuppressWarnings("unchecked")
public class ProjectCreator {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectCreator.class);

    private ProjectService projectService;
    private List<Task> initTasks = Lists.newArrayList();

    public void createSelf() {
        Project project = new Project();
        project.setAlias("闪电狗");
        project.setName("flash_dog");
        MongoDbAppender appender = findMongoAppender();
        Assert.notNull(appender, "can't find a MongoDbAppender ,please config one in log4j.properties");
        project.setLogCollection(appender.getCollectionName());
        project.setMongoUri(String.format("mongodb://%s:%s/%s",
                appender.getHostname(), appender.getPort(), appender.getDatabaseName()));

        logger.debug("try create a monitor project for flash-dog {}", project);
        if (projectService.findProject(project.getName()) == null) {
            for (Task task : initTasks)
                project.getTasks().add(renderTask(task, project));
            projectService.saveProject(project);
        } else {
            logger.debug("projectName={} has exist ,skip create ", project.getName());
        }
    }

    private MongoDbAppender findMongoAppender() {
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Object appender = appenders.nextElement();
            if (appender instanceof MongoDbAppender) {
                return (MongoDbAppender) appender;
            }
        }
        return null;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setInitTasks(List<Task> initTasks) {
        this.initTasks = initTasks;
    }

    public Task renderTask(Task task, Project project) {
        Task task1 = new Task();
        task1.setCron(task.getCron());
        task1.setName(task.getName());
        task1.setTimeout(task.getTimeout());
        String script = task.getScript();
        script = StringUtils.replace(script, "$project.logCollection", project.getLogCollection());
        script = StringUtils.replace(script, "$project.metricCollection", project.getMetricCollection());
        task1.setScript(script);
        return task1;
    }
}
