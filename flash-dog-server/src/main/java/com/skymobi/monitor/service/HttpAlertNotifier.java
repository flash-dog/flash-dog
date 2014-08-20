package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.util.SystemConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * send alert data to third monitor server
 *
 * @author Hill.Hu
 */
@SuppressWarnings("unchecked")
public class HttpAlertNotifier extends AbstractAlertNotifier implements AlertListener {
    private static Logger logger = LoggerFactory.getLogger(HttpAlertNotifier.class);
    public static final String HTTP_NOTIFY_CONFIG_URL = "httpNotifyConfig_url";
    public static final String HTTP_NOTIFY_CONFIG_TEMPLATE = "httpNotifyConfig_template";
    public static final String HTTP_NOTIFY_CONFIG_PROPERTIES = "httpNotifyConfig_properties";
    public static final String HTTP_NOTIFY_CONFIG_ENABLE = "httpNotifyConfig_enable";

    private HttpClient httpClient = new HttpClient();
    @Resource
    private ProjectService projectService;
    private static final String HTTP_NOTIFY_CONFIG_ENCODE = "httpNotifyConfig_encode";
    @Resource
    private VelocityEngine velocityEngine;

    @Override
    public void _notify(Alert alert) {
        try {
            Project project = projectService.findProject(alert.getProjectName());
            Properties properties = project.getProperties();
//            if (properties.getProperty(HTTP_NOTIFY_CONFIG_ENABLE, "false").equalsIgnoreCase("false"))
//                return;
            String url = properties.getProperty(HTTP_NOTIFY_CONFIG_URL, null);
            if(url==null || url.length()<3)
                return;
            Assert.hasLength(url, "notify url can't be null");
            Map templateVars = new HashMap();
            templateVars.putAll(properties);
            templateVars.put("title", alert.getTitle());
            templateVars.put("content", alert.getContent());
            templateVars.put("createTime", alert.getCreateTime());
            templateVars.put("ip", alert.getIp());
            PostMethod httpMethod = new PostMethod();
            httpMethod.setURI(new HttpURL(url));
            String template = properties.getProperty(HTTP_NOTIFY_CONFIG_TEMPLATE);
            Properties configProperties = new Properties();
            configProperties.load(new StringReader(properties.getProperty(HTTP_NOTIFY_CONFIG_PROPERTIES, "")));
            templateVars.putAll(configProperties);
            String data = renderTemplate(template, templateVars);
            RequestEntity requestEntity = new ByteArrayRequestEntity(data.getBytes(properties.getProperty(HTTP_NOTIFY_CONFIG_ENCODE, "utf-8")));
            httpMethod.setRequestEntity(requestEntity);

            logger.info("notify alert to {} ,data={}", url, data);
            int status = httpClient.executeMethod(httpMethod);
            logger.info("send alert   to third server ={} ,statusCode={} ,response {}", new Object[]{url, status, httpMethod.getResponseBodyAsString()});
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    public String renderTemplate(String template, Map map) {
        Writer out = new StringWriter();
        Context context = new VelocityContext(map);
        map.put("esc", new org.apache.velocity.tools.generic.EscapeTool());
        map.put("datetool", new org.apache.velocity.tools.generic.DateTool());

        try {
            velocityEngine.evaluate(context, out, this.getClass().getName(), template);

        } catch (IOException e) {
            logger.error("render template  fail", e);
        }
        return out.toString();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
