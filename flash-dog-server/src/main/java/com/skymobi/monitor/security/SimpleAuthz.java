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
package com.skymobi.monitor.security;

import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.taglibs.velocity.Authz;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hill.Hu
 * @see Authz
 */
@SuppressWarnings("unused")
public class SimpleAuthz {
    private static Logger logger = LoggerFactory.getLogger(SimpleAuthz.class);
    public static final String USER_PRINCIPAL = "userPrincipal";

    @Resource
    private ProjectService projectService;

    @Resource
    private UserManager userManager;

    /**
     * Get the username of the user
     *
     * @return the username of the user
     */
    public String getPrincipal() {
        Principal obj = getUserPrincipal();

        if (obj != null) {
            return obj.getName();
        } else {
            return "guest";
        }
    }

    public boolean isAuthenticated() {
        return getUserPrincipal().isAuthenticated() && ! "anonymousUser".equalsIgnoreCase(getPrincipal());
    }

    private Authentication getUserPrincipal() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Is the user granted all of the supplied roles
     *
     * @return true if user has all of the listed roles, otherwise false
     */
    public boolean allGranted(String roleList) {
        Set<String> userRoles = getUserRoles();
        String[] roles = roleList.split(",");
        for (String role : roles) {
            if (userRoles.contains(role))
                continue;
            return false;
        }
        return true;
    }

    /**
     * Is the user granted any of the supplied roles
     *
     * @return true if user has any of the listed roles, otherwise false
     */
    public boolean anyGranted(String roleList) {
        Set<String> userRoles = getUserRoles();
        String[] roles = roleList.split(",");
        for (String role : roles) {
            if (userRoles.contains(role))
                return true;
        }
        return false;
    }

    /**
     * is the user granted none of the supplied roles
     *
     * @return true only if none of listed roles are granted
     */
    public boolean noneGranted(String roleList) {
        Set<String> userRoles = getUserRoles();
        String[] roles = roleList.split(",");
        for (String role : roles) {
            if (userRoles.contains(role))
                return false;
        }
        return true;

    }


    private Set<String> getUserRoles() {
//        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Set<String> roles = new HashSet<String>();
//        if (obj instanceof UserDetails) {
//            Collection<? extends GrantedAuthority> gas = ((UserDetails) obj).getAuthorities();
//            for (GrantedAuthority ga : gas) {
//                roles.add(ga.getAuthority());
//            }
//        }
//        return roles;
        Set<String> roles = new HashSet<String>();


        return roles;
    }

    public boolean hasProject(Project project) {
        String principal = getPrincipal();
        return userManager.isSystemAdmin(principal) || project.hasMember(principal);
    }
   public boolean  isAdmin(){
       String principal = getPrincipal();
       return userManager.isSystemAdmin(principal);
   }
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
