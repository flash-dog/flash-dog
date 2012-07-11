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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * author: Hill.Hu
 * send alert data to third monitor server
 */
@Component
@SuppressWarnings("unchecked")
public class HttpAlertNotifier implements AlertListener {
    private static Logger logger = LoggerFactory.getLogger(HttpAlertNotifier.class);
    public static final String HTTP_NOTIFY_CONFIG_URL =  "httpNotifyConfig_url";
    public static final String HTTP_NOTIFY_CONFIG_TEMPLATE = "httpNotifyConfig_template";
    public static final String HTTP_NOTIFY_CONFIG_PROPERTIES = "httpNotifyConfig_properties";

    private HttpClient httpClient = new HttpClient();
    @Resource
    private ProjectService projectService;
    private static final String HTTP_NOTIFY_CONFIG_ENCODE = "httpNotifyConfig_encode";

    @Override
    public void notify(Alert alert) {
        try {
            Project project = projectService.findProject(alert.getProjectName());
            Properties properties = project.getProperties();

            String url = properties.getProperty(HTTP_NOTIFY_CONFIG_URL, null);
            if (url == null)
                return;
            Map templateVars = new HashMap();
            templateVars.putAll(properties);
            templateVars.put("title", alert.getTitle());
            templateVars.put("content", alert.getContent());
            templateVars.put("time", sdf.format(alert.getCreateTime()));
            PostMethod httpMethod = new PostMethod();
            httpMethod.setURI(new HttpURL(url));
            String template = properties.getProperty(HTTP_NOTIFY_CONFIG_TEMPLATE);
            Properties configProperties=new Properties();
            configProperties.load(new StringReader(properties.getProperty(HTTP_NOTIFY_CONFIG_PROPERTIES,"")));
            templateVars.putAll(configProperties);
            String data = renderTemplate(template, templateVars);
            RequestEntity requestEntity = new ByteArrayRequestEntity(data.getBytes(properties.getProperty(HTTP_NOTIFY_CONFIG_ENCODE,"utf-8")));
            httpMethod.setRequestEntity(requestEntity);

            logger.debug("notify alert to {} ,data={}", url, data);
            int status = httpClient.executeMethod(httpMethod);
            logger.info("send alert   to third server ={} ,statusCode={} ,response {}",new Object[]{ url, status,httpMethod.getResponseBodyAsString()});
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd h:m:ss");

    public String renderTemplate(String template, Map map) {
        for (Object key : map.keySet()) {
            String value = "";
            if (map.containsKey(key))
                value = map.get(key) + "";
            template = StringUtils.replace(template, "$" + key, value);

        }

        return template;
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

}
