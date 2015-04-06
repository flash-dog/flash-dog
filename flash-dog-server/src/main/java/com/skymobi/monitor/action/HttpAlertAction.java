package com.skymobi.monitor.action;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.WebResult;
import com.skymobi.monitor.service.HttpAlertNotifier;
import com.skymobi.monitor.util.SerializableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Hill.Hu
 */
@Controller
public class HttpAlertAction {
    @Resource
    private HttpAlertNotifier httpAlertNotifier;
    @Resource
    SerializableResourceBundleMessageSource messageBundle;

    @RequestMapping(value = "/projects/{projectName}/notifier/http/test")
    public @ResponseBody
    WebResult notify(HttpServletRequest request,
                                             @RequestBody Properties properties, @PathVariable String projectName) {
        WebResult result=new WebResult();
        try {
            Alert alert =new Alert();
            alert.setTitle("test");
            alert.setContent("this is a alert test");
            alert.setIp(request.getLocalAddr());
            String data = httpAlertNotifier.notify(alert, properties);
            String url = properties.getProperty(HttpAlertNotifier.HTTP_NOTIFY_CONFIG_URL, null);

            result.setMessage("send alert to "+url+",data:"+data);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
       return result;
    }
}
