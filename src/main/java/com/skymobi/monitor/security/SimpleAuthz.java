package com.skymobi.monitor.security;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.taglibs.velocity.Authz;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hill.Hu
 * @see Authz
 */
public class SimpleAuthz  {
    private static Logger logger = LoggerFactory.getLogger(Authz.class);
    public static final String USER_PRINCIPAL = "userPrincipal";


    private List<String> admins=Lists.newArrayList();
    @Resource
    private ProjectService projectService;

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

    private Principal getUserPrincipal() {

        return (Principal) RequestContextHolder.getRequestAttributes().getAttribute(USER_PRINCIPAL, RequestAttributes.SCOPE_REQUEST);
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
        String [] roles=roleList.split(",");
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

    public boolean hasProject(Project project){
        String principal = getPrincipal();
        return admins.contains(principal) ||project.hasMember(principal);
    }
    


    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }
}
