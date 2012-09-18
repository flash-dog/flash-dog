package com.skymobi.monitor.action;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.HttpAlertNotifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Hill.Hu
 */
@Controller
public class HttpAlertAction {
    @Resource
    private HttpAlertNotifier httpAlertNotifier;

    @RequestMapping(value = "/projects/{projectName}/notifier/http/test")
    public @ResponseBody boolean listWarnings(Alert alert, @PathVariable String projectName) throws IOException {

        try {
            httpAlertNotifier.notify(alert);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
