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
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.View;
import com.skymobi.monitor.model.WebResult;
import com.skymobi.monitor.security.User;
import com.skymobi.monitor.security.UserManager;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.service.ViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: Hill.Hu
 */
@Controller
public class AdminAction {
    private static Logger logger = LoggerFactory.getLogger(AdminAction.class);
    @Resource
    private UserManager userManager;
    @Resource
    private ViewService viewService;
     @Resource
     private ProjectService projectService;
    @RequestMapping(value = "/admin/user/list", method = RequestMethod.GET)
    public String show(ModelMap map) {
        List<User> users = userManager.listUsers();

        map.put("users", users);
        return "user/list";
    }

    @RequestMapping(value = "/admin/user/", method = RequestMethod.POST)
    public String update(User user, ModelMap map) {
        userManager.monitorUser(user);
        map.put("user", user);
        return "redirect:/admin/user/list";
    }

    @RequestMapping(value = "/admin/views/", method = RequestMethod.POST)
    public String addView(View view, String projectName) {
        if (projectName != null)
            view.setProjectNames(Lists.newArrayList(projectName.split(",")));
        logger.debug("save view ={}",view);
        viewService.saveView(view);

        return "redirect:/projects";
    }

    @RequestMapping(value = "/admin/views/destroy" )
    public @ResponseBody  WebResult deleteView(String name,  ModelMap map) {

        viewService.delete(name);

        return new WebResult();
    }


    @RequestMapping(value = "/admin/views/save")
    public @ResponseBody
    WebResult createView(HttpEntity<View> entity,  ModelMap map) {
        WebResult result=new WebResult();
        try{
            View view=entity.getBody();
            Assert.isTrue(view.getName().length() > 0,"name should not be null");
            logger.debug("save view ={}",view);
            viewService.saveView(view);
        } catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }



        return result;
    }
}
