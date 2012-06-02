package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Log;
import com.skymobi.monitor.model.LogQuery;
import com.skymobi.monitor.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.text.ParseException;
import java.util.List;

/**
 * @author Hill.Hu
 *         日志分析服务
 */
@Service
public class LogsService {
    private static Logger logger = LoggerFactory.getLogger(LogsService.class);

    @Resource
    ProjectService projectService;
    @Resource
    TaskService taskService;
    private int max = 100;


    public List<Log> findLogs(String projectName, LogQuery logQuery) throws ParseException {
        Project project = projectService.findProject(projectName);
        MongoTemplate template = project.fetchMongoTemplate();
        Query query = new BasicQuery(logQuery.toQuery());
        query.limit(max);

        query.sort().on("$natural", Order.DESCENDING);
        logger.debug("find logs from {}  by query {}", project.getLogCollection(), query.getQueryObject());

        return template.find(query, Log.class, project.getLogCollection());
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
