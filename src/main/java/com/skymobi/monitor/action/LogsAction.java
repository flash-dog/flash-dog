package com.skymobi.monitor.action;

import com.skymobi.monitor.model.Log;
import com.skymobi.monitor.model.LogQuery;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.LogsService;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Author: Hill.Hu
 * Date: 11-11-23 上午8:51
 */
@Controller
public class LogsAction {
    private static Logger logger = LoggerFactory.getLogger(LogsAction.class);

    @Resource
    private ProjectService projectService;

    @Resource
    private TaskService taskService;

    @Resource
    private LogsService logsService;

    @RequestMapping(value = "/projects/{projectName}/logs", method = RequestMethod.GET)
    public String test(ModelMap map, @PathVariable String projectName) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        Project project = projectService.findProject(projectName);
        map.put("project", project);
        return "logs/show";
    }

    @RequestMapping(value = "/projects/{projectName}/logs/more", method = RequestMethod.GET)
    public String console(ModelMap map, @PathVariable String projectName,LogQuery logQuery) throws IOException, ParseException {

        Project project = projectService.findProject(projectName);
        map.put("project", project);
        List<Log> logs=logsService.findLogs(projectName,logQuery);
        map.put("logs",logs);
        return "logs/show";
    }

}
