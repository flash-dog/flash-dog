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

import com.google.gson.Gson;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import com.skymobi.monitor.model.WebResult;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
* @author hill.hu
 *
 */
@Controller
public class TaskAction {
    private static Logger logger = LoggerFactory.getLogger(TaskAction.class);

    @Resource
    private ProjectService projectService;


    /**
     * 显示任务列表
     *
     * @param map
     * @param projectName
     * @return
     */
    @RequestMapping(value = "/projects/{projectName}/tasks", method = RequestMethod.GET)
    public String show(ModelMap map, @PathVariable String projectName) throws IOException {
        Project project = projectService.findProject(projectName);

        map.put("project", project);
        map.put("tasks", project.getTasks());
        return "task/list";
    }

    /**
     * 创建任务
     *
     * @param map
     * @param projectName
     * @return
     */
    @RequestMapping(value = "/projects/{projectName}/tasks/new", method = RequestMethod.POST)
    public String create(ModelMap map, @PathVariable String projectName, String taskName) {

        Project project = projectService.findProject(projectName);
        Task projectTask = project.findTask(taskName);
        if (projectTask == null) {
            Task task = new Task();
            task.setName(taskName);
            project.saveTask(task);
            projectService.saveProject(project);
        }
        return String.format("redirect:/projects/%s/tasks/%s", projectName, taskName);

    }

    /**
     * 编辑任务
     *
     * @param map
     * @param projectName
     * @param taskName
     * @return
     */
    @RequestMapping(value = "/projects/{projectName}/tasks/{taskName}", method = RequestMethod.GET)
    public String edit(ModelMap map, @PathVariable String projectName, @PathVariable String taskName) throws IOException {
        Project project = projectService.findProject(projectName);
        Task task = project.findTask(taskName);
        map.put("project", project);
        map.put("task", task);
        return "task/edit";
    }

    @RequestMapping(value = "/projects/{projectName}/tasks/update", method = RequestMethod.POST)
    public @ResponseBody
    WebResult updateTask(ModelMap map, @PathVariable String projectName, HttpEntity<Task> entity) {
        Task task=entity.getBody();
        logger.debug("update task {}", new Gson().toJson(task));
        projectService.saveTask(projectName, task);

        return new WebResult();
    }

    /**
     * 更新任务
     *
     * @param map
     * @param projectName
     * @param task
     * @return
     */
    @RequestMapping(value = "/projects/{projectName}/tasks", method = RequestMethod.POST)
    public String update(ModelMap map, @PathVariable String projectName, Task task) {
        logger.debug("update task {}", new Gson().toJson(task));
        projectService.saveTask(projectName, task);

        return String.format("redirect:/projects/%s/tasks", projectName);
    }
    @RequestMapping(value = "/projects/{projectName}/tasks/{taskName}/destroy")
    public @ResponseBody WebResult deleteTask(ModelMap map, @PathVariable String projectName, @PathVariable String taskName) throws IOException {
        projectService.removeTask(projectName, taskName);

        return new WebResult();

    }

}
