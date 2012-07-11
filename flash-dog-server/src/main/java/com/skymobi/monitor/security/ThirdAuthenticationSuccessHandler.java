package com.skymobi.monitor.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: Hill.Hu
 * login from third auth ,such as cas
 */
public class ThirdAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static Logger logger = LoggerFactory.getLogger(ThirdAuthenticationSuccessHandler.class);

    private UserManager userManager;
    private String indexUrl = "projects";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        Principal userPrincipal = request.getUserPrincipal();
//        Assert.notNull(userPrincipal);
//        request.setAttribute(SimpleAuthz.USER_PRINCIPAL, userPrincipal);
        String name = authentication.getName();
        request.setAttribute("userName", name);
        User user = userManager.loadUserByUsername(name);
        if (user == null) {
            logger.info("register a new user from other auth ,username={}", name);
            userManager.registerUser(new User(name, User.NO_PASSWORD, true));
        } else {
            response.sendRedirect(indexUrl);
        }

    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }
}
