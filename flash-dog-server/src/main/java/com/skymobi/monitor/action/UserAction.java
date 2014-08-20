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

import com.skymobi.monitor.model.WebResult;
import com.skymobi.monitor.security.RegisterException;
import com.skymobi.monitor.security.User;
import com.skymobi.monitor.security.UserManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Author: Hill.Hu
 */
@Controller
public class UserAction {
    private static Logger logger = LoggerFactory.getLogger(UserAction.class);
    @Resource
    private UserManager userManager;

    @RequestMapping(value = "/members/search", method = RequestMethod.GET)
    public String show(ModelMap map) {
        List<User> users = userManager.listUsers();

        map.put("users", users);
        return "";
    }
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public @ResponseBody
    WebResult listAll(ModelMap map) {
        List<User> users = userManager.listUsers();
        WebResult result=new WebResult(users);

        return result;
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap map,HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return "app/login";
    }

    @RequestMapping(value = "/check")
    public String check(User user, ModelMap map,HttpServletRequest request) throws UnsupportedEncodingException {
        logger.debug("try to login by {} ",user);
        User _user = userManager.loadUserByUsername(user.getUsername());
        if (_user == null) {
            map.put("flashMsg", "user or password error");
            return "user/login";
        }
        if (!StringUtils.equals(user.getPassword(), _user.getPassword())) {
            map.put("flashMsg", "err.password_err");
            return "user/login";
        }
        if (!_user.isEnabled()) {
            return "redirect:/user/wait?username=" + URLEncoder.encode(user.getUsername(), "utf-8");
        }
        return "redirect:/projects";
    }

    @RequestMapping(value = "/user/new", method = RequestMethod.GET)
    public String register(ModelMap map) {

        return "user/new";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String register(User user, HttpServletRequest request,ModelMap mm) throws UnsupportedEncodingException {
        user.setEnabled(false);
        //spring mvc can't auto set password by  ,why?
        user.setPassword(request.getParameter("password"));

        mm.put("user", user);
        try {
            userManager.registerUser(user);

            return "redirect:/user/wait?username=" + URLEncoder.encode(user.getUsername(), "utf-8");
        } catch (RegisterException e) {

            mm.put("flashMsg", e.getMessage());
            return "user/new";
        }

    }
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public @ResponseBody WebResult   update(HttpEntity<User> entity, HttpServletRequest request,ModelMap mm) throws UnsupportedEncodingException {
       User user=entity.getBody();
        WebResult result=new WebResult();

        try {
            if(userManager.loadUserByUsername(user.getUsername())==null){
                userManager.registerUser(user);
            }else{
                userManager.monitorUser(user);
            }


        } catch (IllegalArgumentException e) {
            result.setSuccess(false);

            result.setMessage(e.getMessage());
        }
       return result;
    }
    @RequestMapping(value = "/user/destroy", method = RequestMethod.POST)
    public @ResponseBody WebResult   delete(HttpEntity<User> entity) throws UnsupportedEncodingException {
        User user=entity.getBody();
        WebResult result=new WebResult();

        userManager.removeUser(user.getUsername());
        return result;
    }
    @RequestMapping(value = "/user/wait", method = RequestMethod.GET)
    public String wait(User user, ModelMap mm) {
        mm.put("user", user);
        return "user/wait";
    }
}
