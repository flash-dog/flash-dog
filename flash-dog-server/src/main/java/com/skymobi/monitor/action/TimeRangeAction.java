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

import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.TimeRange;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
* @author hill.hu
 *
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
