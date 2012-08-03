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

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.security.SimpleAuthz;
import com.skymobi.monitor.service.AlertService;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.util.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-11-23 上午8:51
 * 项目转发器
 */
@SuppressWarnings("unchecked")
@Controller
public class ProjectAction {
    private static Logger logger = LoggerFactory.getLogger(ProjectAction.class);

    @Resource
    private ProjectService projectService;

    @Resource
    private SimpleAuthz simpleAuthz;

    @Resource
    private AlertService alertService;

    @RequestMapping({"/index", "/"})
    public String index(ModelMap map, HttpServletResponse response) throws IOException {

        return "redirect:/projects";
    }

    /**
     * 查看项目列表
     *
     * @param map
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public String listProject(ModelMap map, HttpServletResponse response) throws IOException {
        List<Project> projects = projectService.findProjects();
        map.put("projects", projects);
        List<String> warningProjectNames= alertService.findWarningProjects();
        map.put("warningProjectNames",warningProjectNames);
        return "project/list";
    }

    /**
     * 进入创建项目页面
     *
     * @param map
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/projects/new", method = RequestMethod.GET)
    public String create(ModelMap map, HttpServletResponse response) throws IOException {

        return "project/new";
    }

    /**
     * 创建项目
     *
     * @param map
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    public String save(ModelMap map, HttpServletResponse response, Project project,  BindingResult bindingResult) throws IOException {

        String userName = simpleAuthz.getPrincipal();
        project.setAdmins(Lists.newArrayList(userName));

        project.setMetricCollection(project.getMetricCollection());
        if (projectService.findProject(project.getName()) == null) {
            projectService.saveProject(project);
            return "redirect:/projects/" + project.getName();
        } else {
            map.put("project",project);
            bindingResult.rejectValue("name", "project name has exist");
            map.put("flashMsg","project  "+project.getName()+ " has exist");
            return "project/new";
        }
    }

    /**
     * 查看项目
     *
     * @param map
     * @param name
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/projects/{name}", method = RequestMethod.GET)
    public String show(ModelMap map, @PathVariable String name) throws IOException {
        Project project = projectService.findProject(name);
        map.put("project", project);
        map.put("metricNames", project.findMetricNames());
        Map views = project.getViews();
        for(String MetricName: project.findMetricNames())
        	views.put(MetricName, MetricName);
        map.put("views", project.getViews());
        return "project/show";
    }

    /**
     * 进入设置项目页面
     *
     * @param map
     * @param name
     * @return
     * @throws IOException
     */
    @RequestMapping("/projects/{name}/settings")
    public String edit(ModelMap map, @PathVariable String name) throws IOException {
        Project project = projectService.findProject(name);
        map.put("project", project);
        return "project/settings";
    }

    /**
     * 进入设置项目页面
     *
     * @param map
     * @param name
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/projects/{name}/settings/{module}", method = RequestMethod.GET)
    public String settings(ModelMap map, @PathVariable String name, @PathVariable String module) throws IOException {
        Project project = projectService.findProject(name);
        map.put("project", project);
        map.put("module", module);
        return "project/settings";
    }

    /**
     * 更新指定项目
     *
     * @param map
     * @param name
     * @return
     */
    @RequestMapping(value = "/projects/{name}/info", method = RequestMethod.POST)
    public String update(ModelMap map, @PathVariable String name, Project project) throws IOException {
        Project dbProject = projectService.findProject(name);
        dbProject.setMailList(project.getMailList());
        dbProject.setLogCollection(project.getLogCollection());
        dbProject.setMongoUri(project.getMongoUri());
        dbProject.setAlias(project.getAlias());
        dbProject.setMetricCollection(project.getMetricCollection());
        projectService.saveProject(dbProject);

        return "redirect:/projects/" + name + "/settings/info";
    }

    @RequestMapping(value = "/projects/{name}/members", method = RequestMethod.POST)
    public String update(ModelMap map, @PathVariable String name, String admins) throws IOException {
        Project dbProject = projectService.findProject(name);
        dbProject.setAdmins(Lists.newArrayList(admins.split(",")));

        projectService.saveProject(dbProject);

        return "redirect:/projects/" + name + "/settings/members";
    }

    @RequestMapping(value = "/projects/{name}/ext", method = RequestMethod.POST)
    public String updateExt(ModelMap map, @PathVariable String name, HttpServletRequest request) throws IOException {
        Project dbProject = projectService.findProject(name);
        Map<String, Object> extProperties = WebUtils.getParametersStartingWith(request, SystemConstants.CONFIG_PREFIX);
        logger.debug("update project ext properties {}", extProperties);
        dbProject.getProperties().putAll(extProperties);
        projectService.saveProject(dbProject);

        return "redirect:/projects/" + name + "/settings/ext";
    }

    @RequestMapping(value = "/projects/{projectName}/destroy")
    public String delete(@PathVariable String projectName) throws IOException {
        projectService.remove(projectName);

        return "redirect:/projects";

    }

}
