package com.skymobi.monitor.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import com.skymobi.monitor.service.ProjectService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.texen.util.FileUtil;
import org.log4mongo.AsynMongoURILayoutAppender;
import org.log4mongo.MongoDbAppender;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * author: Hill.Hu
 */
@SuppressWarnings("unchecked")
public class ProjectCreator {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectCreator.class);

    private ProjectService projectService;
    private List<Task> initTasks = Lists.newArrayList();
    @Resource
    private VelocityEngine velocityEngine;

    public void createSelf() {
        Project project = new Project();
        project.setAlias("闪电狗");
        project.setName("flash_dog");
        setMongoInfoByLog4j(project);

        if (projectService.findProject(project.getName()) == null) {
            logger.debug("try create a monitor project for flash-dog {}", project);
            project.setTasks(initTasks);
            create(project);

        } else {
            logger.debug("projectName={} has exist ,skip create ", project.getName());
        }

    }

    public void create(Project project) {
        Assert.isNull(projectService.findProject(project.getName()), "project  [" + project.getName() + "] has exist");
        MongoTemplate template = project.fetchMongoTemplate();
        Assert.notNull(template, "mongo uri is not access");
        Assert.notNull(template.getDb(), "mongo uri is not access");
        Assert.isTrue(project.fetchMongoTemplate().collectionExists(project.getLogCollection()), " [" + project.getLogCollection() + "] 日志表不存在");
        try {
            List<Task> taskList = Lists.newArrayList();
            logger.debug("init task count:{}", project.getTasks().size());
            for (Task _task : project.getTasks()) {

                Task task = getTemplateTask(_task);
                Task tempTask = renderTemplateTask(task, project);
                if (tempTask != null)
                    taskList.add(tempTask);
            }
            project.setTasks(taskList);
        } catch (Exception e) {
            logger.error("", e);
            throw new IllegalArgumentException("自动添加监控脚本错误:" + e.getMessage());
        }
        projectService.saveProject(project);
    }

    private Task getTemplateTask(Task task) {
        for (Task task1 : initTasks) {
            if (task1.getName() != null && task1.getName().equals(task.getName())) {
                return task1;
            }
        }
        return null;
    }

    /**
     * config mongo uri from  log4jProperties
     * @param project
     */
    private void setMongoInfoByLog4j(Project project) {
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Object appender = appenders.nextElement();
            if (appender instanceof MongoDbAppender) {
                MongoDbAppender ref = (MongoDbAppender) appender;
                project.setLogCollection(ref.getCollectionName());
                project.setMongoUri(String.format("mongodb://%s:%s/%s",
                        ref.getHostname(), ref.getPort(), ref.getDatabaseName()));
            }
            if (appender instanceof AsynMongoURILayoutAppender) {
                AsynMongoURILayoutAppender ref = (AsynMongoURILayoutAppender) appender;
                project.setLogCollection(ref.getCollectionName());
                project.setMongoUri(ref.getMongoURI());
            }
        }
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setInitTasks(List<Task> initTasks) {
        this.initTasks = initTasks;
    }

    public Task renderTemplateTask(Task task, Project project) {
        try {

            Task task1 = new Task();
            task1.setCron(task.getCron());
            task1.setName(task.getName());
            task1.setTimeout(task.getTimeout());
            Writer out = new StringWriter();
            File file = getTemplateFile(task1.getName());

            String temp = FileUtils.readFileToString(file, "utf-8");
            Map map = Maps.newHashMap();
            map.put("project", project);

            Context context = new VelocityContext(map);
            velocityEngine.evaluate(context, out, this.getClass().getName(), temp);
            task1.setScript(out.toString());
            return task1;
        } catch (Exception e) {
            logger.error("init task from template fail:", e);
            return null;
        }


    }

    private File getTemplateFile(String name) throws FileNotFoundException {
        File file = ResourceUtils.getFile("src/main/webapp/WEB-INF/content/task/templates/" + name + ".vm");
        if (file.exists())
            return file;
        return ResourceUtils.getFile("classpath:webapp/WEB-INF/content/task/templates/" + name + ".vm");
    }
}
