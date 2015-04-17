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
import com.skymobi.monitor.model.*;
import com.skymobi.monitor.security.SimpleAuthz;
import com.skymobi.monitor.service.AlertService;
import com.skymobi.monitor.service.ViewService;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.util.ProjectCreator;
import com.skymobi.monitor.util.SerializableResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author hill.hu
 *         <p/>
 *         项目转发器
 */
@SuppressWarnings("unchecked")
@Controller
public class ProjectAction {
    private static Logger logger = LoggerFactory.getLogger(ProjectAction.class);

    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectCreator projectCreator;
    @Resource
    private SimpleAuthz simpleAuthz;

    @Resource
    private AlertService alertService;

    @Resource
    private ViewService viewService;
    @Resource
    SerializableResourceBundleMessageSource messageBundle;

    @RequestMapping({"/index", "/"})
    public String index(ModelMap map, HttpServletResponse response) throws IOException {

        return "app/index";
    }

    /**
     * @deprecated
     */
    @RequestMapping({"/projects"})
    public String projects(ModelMap map, HttpServletResponse response) throws IOException {

        return "redirect:/";
    }

    @RequestMapping(value = "/project/list", method = RequestMethod.GET)
    public
    @ResponseBody
    ModelMap list(ModelMap map, HttpServletRequest request) throws IOException {
        List<Project> projects = projectService.findProjects();
        for (Project project : projects) {
            fixAlias(project, request);
        }
        map.put("projects", projects);
        List<View> views = viewService.findAll();
        map.put("views", views);
        return map;
    }

    private void fixAlias(Project project, HttpServletRequest request) {
        if (project.getName().equalsIgnoreCase("flash_dog")) {
            String message = messageBundle.getMessage("app.title", null, request.getLocale());
            project.setAlias(message);

        }
    }

    /**
     * 创建项目
     *
     * @return
     * @throws IOException
     */

    @RequestMapping(value = "/projects/add", method = RequestMethod.POST)
    public
    @ResponseBody
    WebResult add(@RequestBody Project project) throws IOException {
//        Project project =entity.getBody();
        String userName = simpleAuthz.getPrincipal();
        project.setAdmins(Lists.newArrayList(userName));
        WebResult result = new WebResult();
        project.setMetricCollection(project.getMetricCollection());
        try {

            projectCreator.create(project);

        } catch (IllegalArgumentException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            logger.debug("", e);
        }
        return result;
    }


    @RequestMapping(value = "/project/{name}", method = RequestMethod.GET)
    public
    @ResponseBody
    ModelMap showProject(ModelMap map, @PathVariable String name,HttpServletRequest request) throws IOException {
        Project project = projectService.findProject(name);
        map.put("project", project);
        map.put("metricNames", project.findMetricNames());
        fixAlias(project, request);
        return map;
    }

    /**
     * 更新指定项目
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/projects/{name}/basic", method = RequestMethod.POST)
    public
    @ResponseBody
    WebResult updateBasic(@PathVariable String name, HttpEntity<Project> entity) throws IOException {
        Project project = entity.getBody();
        Project dbProject = projectService.findProject(name);
        dbProject.setMailList(project.getMailList());
        dbProject.setLogCollection(project.getLogCollection());
        dbProject.setMongoUri(project.getMongoUri());
        dbProject.setAlias(project.getAlias());
        dbProject.setMetricCollection(project.getMetricCollection());
        dbProject.setAdmins(project.getAdmins());
        projectService.saveProject(dbProject);

        return new WebResult();
    }


    @RequestMapping(value = "/projects/{name}/ext", method = RequestMethod.POST)
    public
    @ResponseBody
    WebResult updateNotify(@PathVariable String name, HttpServletRequest request, HttpEntity<Map> httpEntity) throws IOException {

        Map map = httpEntity.getBody();
        Project dbProject = projectService.findProject(name);
        dbProject.getProperties().putAll(map);
        logger.debug("update project ext properties {}", map);

        projectService.saveProject(dbProject);

        return new WebResult();
    }

    @RequestMapping(value = "/projects/{projectName}/destroy")
    public
    @ResponseBody
    WebResult delete(@PathVariable String projectName) throws IOException {
        projectService.remove(projectName);

        return new WebResult();

    }

}
