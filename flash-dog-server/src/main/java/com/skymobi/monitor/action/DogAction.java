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

import com.skymobi.monitor.model.*;
import com.skymobi.monitor.service.AlertService;
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
import java.util.List;

/**
* @author hill.hu
 *
 */
@Controller
public class DogAction {
    private static Logger logger = LoggerFactory.getLogger(DogAction.class);

    @Resource
    private ProjectService projectService;

    @Resource
    AlertService alertService;



    @RequestMapping(value = "/projects/{projectName}/dog", method = RequestMethod.POST)
    public @ResponseBody WebResult update( @PathVariable String projectName, HttpEntity<MetricDog > entity)   {
        MetricDog metricDog=entity.getBody();
        Project project = projectService.findProject(projectName);
        project.saveDog(metricDog);
        projectService.saveProject(project);

        return  new WebResult(project.getMetricDogs());
    }


    @RequestMapping(value = "/projects/{projectName}/dog/destroy")
    public  @ResponseBody WebResult  remove(@PathVariable String projectName,  String dogName)  {
        Project project = projectService.findProject(projectName);

        project.removeDog(dogName);
        projectService.saveProject(project);
        return  new WebResult(project.getMetricDogs());
    }
}
