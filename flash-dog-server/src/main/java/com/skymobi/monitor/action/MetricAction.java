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
package com.skymobi.monitor.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.skymobi.monitor.model.ChartView;
import com.skymobi.monitor.util.ChartUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.MetricValue;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.TimeRange;
import com.skymobi.monitor.service.ProjectService;

/**
 * @author  Hill.Hu,steven.zheng
 */
@Controller
public class MetricAction {
    private static Logger logger = LoggerFactory.getLogger(MetricAction.class);

    @Resource
    private ProjectService projectService;

    @RequestMapping(value = "/projects/{projectName}/metrics", method = RequestMethod.GET)
    public @ResponseBody ModelMap  renderVar(ModelMap map, @PathVariable String projectName, String metricName,String newChartTitle) {
    	Project project = projectService.findProject(projectName);
    	if(StringUtils.isNotEmpty(newChartTitle)){
	    		project.getViews().put(newChartTitle, metricName);
	    		projectService.saveProject(project);
    	}

        String[] metrics = metricName.split(",");

        List<List<MetricValue>>  metricLists=Lists.newArrayList();
        for(String name:metrics){
            metricLists.add(project.findMetricData(name));
        }

        map.put("data", ChartUtil.format(metricLists));
        return  map;
    }

    @RequestMapping(value = "/projects/{projectName}/metrics/add", method = RequestMethod.POST)
    public @ResponseBody
    ChartView addMetricView(ModelMap map, @PathVariable String projectName,HttpEntity<ChartView> entity ) {
        ChartView chartView=entity.getBody();
        Assert.notNull(chartView.getTitle());
        Project project = projectService.findProject(projectName);
        project.getChartViews().add(chartView);
        projectService.saveProject(project);
        return  chartView;
    }
    @RequestMapping(value = "/projects/{projectName}/metrics/show", method = RequestMethod.GET)
    public @ResponseBody ModelMap  show(ModelMap map, @PathVariable String projectName, String title) {
        Project project = projectService.findProject(projectName);

        List<List<MetricValue>>  metricLists=Lists.newArrayList();
            ChartView view = null;
        for(ChartView chartView:project.getChartViews()){
            if(title.equals(chartView.getTitle()))
            {
                view=chartView;
                break;
            }
        }
        if(view!=null){
            for(String name:view.getMetricNames()){
                metricLists.add(project.findMetricData(name));
            }

            map.put("data", ChartUtil.format(metricLists,false));
        }


        return  map;
    }
    @RequestMapping(value = "/projects/{projectName}/metrics/destroy")
    public @ResponseBody ModelMap  metricsDelete(ModelMap map, @PathVariable String projectName, String title) {
    	Project project = projectService.findProject(projectName);
    	if(StringUtils.isNotEmpty(title)){
            for(int i=0;i<project.getChartViews().size();i++)
    		if(project.getChartViews().get(i).getTitle().equals(title)){
	    		project.getChartViews().remove(i);
	    		projectService.saveProject(project);
    		}
    	}
    	return  map;
    }



    @RequestMapping(value = "/projects/{projectName}/metrics/timeRange", method = RequestMethod.POST)
    public String save(@PathVariable String projectName, TimeRange timeRange) {
        Project project = projectService.findProject(projectName);

        project.setTimeRange(timeRange);

        projectService.saveProject(project);
        return "redirect:/projects/" + projectName;
    }
    @RequestMapping(value = "/projects/{projectName}/setting/timeRange", method = RequestMethod.POST)
    public @ResponseBody String timeRange(@PathVariable String projectName, HttpEntity<TimeRange> entity) {
        TimeRange timeRange=entity.getBody();
        Assert.notNull(timeRange,"time rage should not be null");
        Project project = projectService.findProject(projectName);

        project.setTimeRange(timeRange);

        projectService.saveProject(project);
        return "true";
    }
}
