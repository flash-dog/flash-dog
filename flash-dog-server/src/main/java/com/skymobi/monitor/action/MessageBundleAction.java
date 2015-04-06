package com.skymobi.monitor.action;

import com.skymobi.monitor.util.SerializableResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Properties;

/**
 * @author hill.hu
 */
@Controller
public class MessageBundleAction {
    private static Logger logger = LoggerFactory.getLogger(MessageBundleAction.class);

    @Resource
    SerializableResourceBundleMessageSource messageBundle;

    /**
     * ReadAll
     */
    @RequestMapping(value = "/resource/messages",method = RequestMethod.GET)
    @ResponseBody
    public Properties list(@RequestParam String lang) {

        Properties properties = messageBundle.getAllProperties(new Locale(lang));
        logger.info("load all message sources ,lang={},count={}",lang,properties.size());
        return properties;
    }
}
