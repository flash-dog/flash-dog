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

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author hill.hu
 *
 */
@Controller
public class MongoAction {
    private static Logger logger = LoggerFactory.getLogger(MongoAction.class);

    @Resource
    private ProjectService projectService;

    @Resource
    private TaskService taskService;

    @RequestMapping(value = "/projects/{projectName}/mongo/console", method = RequestMethod.POST)
    public @ResponseBody    BasicDBObject test(ModelMap map, @PathVariable String projectName, String script,Integer timeout) throws IOException, ExecutionException, TimeoutException, InterruptedException {

        Project project = projectService.findProject(projectName);
        FutureTask<CommandResult> futureTask = taskService.runScript(script, project);

        BasicDBObject result = null;
        timeout=timeout!=null?timeout:60;
        try {
            result = futureTask.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("execute task fail "+script,e);
            result=new BasicDBObject("err",e.toString());
        }
        logger.debug("run mongo script =[{}] ,result=[{}]", script,result);

         return result;
    }

    @RequestMapping(value = "/projects/{projectName}/mongo/console", method = RequestMethod.GET)
    public String console(ModelMap map, @PathVariable String projectName) throws IOException {
        Project project = projectService.findProject(projectName);
        map.put("project", project);
        return "mongo/console";
    }

}
