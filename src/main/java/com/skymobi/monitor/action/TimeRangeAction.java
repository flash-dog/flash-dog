package com.skymobi.monitor.action;

import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-11-23 上午8:51
 */
@Controller
public class TimeRangeAction {
    private static Logger logger = LoggerFactory.getLogger(TimeRangeAction.class);

    @Resource
    private ProjectService projectService;


    @RequestMapping(value = "/projects/{projectName}/timeRange", method = RequestMethod.POST)
    public String update(ModelMap map, @PathVariable String projectName,
                         TimeRange timeRange) {
        logger.debug("update  time range to {} of project {} ", timeRange, projectName);
        Project project = projectService.findProject(projectName);

        projectService.saveProject(project);
        return "redirect:/projects/" + projectName;
    }


}
