package com.skymobi.monitor.action;

import com.skymobi.monitor.security.User;
import com.skymobi.monitor.security.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-23 上午10:07
 */
@Controller
public class UserAction {
    @Resource
    private UserManager userManager;

    @RequestMapping(value = "/members/search", method = RequestMethod.GET)
    public String show(ModelMap map) {
        List<User> users = userManager.listUsers();

        map.put("users", users);
        return "";
    }
}
