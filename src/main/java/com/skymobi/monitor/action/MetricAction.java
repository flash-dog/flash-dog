package com.skymobi.monitor.action;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.*;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-11-23 上午8:51
 */
@Controller
public class MetricAction {
    private static Logger logger = LoggerFactory.getLogger(MetricAction.class);

    @Resource
    private ProjectService projectService;
    private SimpleDateFormat sdf=new SimpleDateFormat("M月dd HH:mm");


    @RequestMapping(value = "/projects/{projectName}/metrics", method = RequestMethod.GET)
    public String renderVar(ModelMap map, @PathVariable String projectName,   String metricName) {
        Project project = projectService.findProject(projectName);

        List data = project.findMetricData(metricName);

        logger.info("render metric for name={}",  metricName);
        map.put("project", project);
        List<List> varData = Lists.newArrayList();
        for (Object obj : data) {
            MetricValue mv = (MetricValue) obj;
            if (mv.getName().equals(metricName)) {
                long timeStamp = mv.getTimeStamp();
                varData.add(Lists.newArrayList(sdf.format(new Date(timeStamp)), mv.getValue()));
            }
        }

        map.put("data", varData);
        return "metric/show";
    }
    @RequestMapping(value = "/projects/{projectName}/metrics/timeRange", method = RequestMethod.POST)
    public String save(@PathVariable String projectName, TimeRange timeRange) {
        Project project = projectService.findProject(projectName);

        project.setTimeRange(timeRange);

        projectService.saveProject(project);
        return "redirect:/projects/" + projectName ;
    }

}
