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

import com.skymobi.monitor.model.Chart;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
* @author hill.hu
 *
 * 图表控制器
 */
@Controller
public class ChartAction {
    private static Logger logger = LoggerFactory.getLogger(ChartAction.class);

    @Resource
    private ProjectService projectService;


    @RequestMapping(value = "/projects/{projectName}/charts/{chartName}", method = RequestMethod.GET)
    public String show(ModelMap map, @PathVariable String projectName, @PathVariable String chartName) {
        Project project = projectService.findProject(projectName);
        Chart chart = project.findChart(chartName);

        map.put("project", project);
        map.put("chart", chart);

        return "chart/show";
    }

    @RequestMapping(value = "/projects/{projectName}/charts/{chartName}/render", method = RequestMethod.GET)
    public String render(ModelMap map, @PathVariable String projectName, @PathVariable String chartName) {
        Project project = projectService.findProject(projectName);
        Chart chart = project.findChart(chartName);
        List data = chart.findData();

        logger.info("render metric name={},data={}", chartName, data);

        map.put("data", data);
        return "chart/show";
    }

    @RequestMapping(value = "/projects/{projectName}/charts", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable String projectName) {
        Project project = projectService.findProject(projectName);

        map.put("project", project);
        return "chart/list";
    }

    @RequestMapping(value = "/projects/{projectName}/charts", method = RequestMethod.POST)
    public String update(ModelMap map, @PathVariable String projectName, Chart chart) {
        Project project = projectService.findProject(projectName);
        Assert.notNull("chart name can't be null", chart.getName());
        project.saveChart(chart);
        projectService.saveProject(project);
        map.put("project", project);

        return "chart/list";
    }

    @RequestMapping(value = "/projects/{projectName}/charts/new", method = RequestMethod.GET)
    public String create(ModelMap map, @PathVariable String projectName) {
        Project project = projectService.findProject(projectName);
        map.put("project", project);

        return "chart/edit";
    }

    @RequestMapping(value = "/projects/{projectName}/charts/{chartName}/edit", method = RequestMethod.GET)
    public String edit(ModelMap map, @PathVariable String projectName, @PathVariable String chartName) {
        Project project = projectService.findProject(projectName);
        Chart chart = project.findChart(chartName);
        map.put("project", project);
        map.put("chart", chart);
        return "chart/edit";
    }

    @RequestMapping(value = "/projects/{projectName}/charts/{chartName}/destroy", method = RequestMethod.POST)
    public String destroy(@PathVariable String projectName, @PathVariable String chartName) {
        Project project = projectService.findProject(projectName);
        Chart chart = new Chart(chartName);
        project.getCharts().remove(chart);
        projectService.saveProject(project);

        return "redirect:/projects/" + projectName + "/charts";
    }
}
