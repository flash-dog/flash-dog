package com.skymobi.monitor.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-25 下午4:02
 */
public class UserInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //org.jasig.cas.client.authentication.AttributePrincipalImpl
        if (request.getUserPrincipal() != null) {
            request.setAttribute(SimpleAuthz.USER_PRINCIPAL, request.getUserPrincipal());
            request.setAttribute("userName", request.getUserPrincipal().getName());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);

    }
}
