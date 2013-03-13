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
package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 扫描各个项目发件箱，并发出邮件
 *
 * @author hill.hu
 */
public class EmailService extends AbstractAlertNotifier implements AlertListener {
    private static Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private ProjectService projectService;
    private boolean enable = true;
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private String from;

    public void _notify(Alert alert) {
        Project project = projectService.findProject(alert.getProjectName());
        if (project == null) {
            logger.error("projectName of  alert is null, alert={}", alert);
            return;
        }
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setText(alert.getContent());
        sm.setSubject(alert.getTitle());
        sm.setFrom(from);
        String mailList=alert.getMetricDog().getMailList();
        if(StringUtils.isBlank(mailList))
        	mailList = project.getMailList();
        if (StringUtils.isNotBlank(mailList))
            sm.setTo(mailList.split(","));
        else
            logger.error("send mail fail ,because mail list is null");
        send(sm);

    }

    public void send(final SimpleMailMessage msg) {
        if (!enable)
            return;
        logger.debug("send mail {}", msg);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                	logger.debug("send mail 2 {}", msg);
                    mailSender.send(msg);
                } catch (MailException e) {
                    logger.error("send msg fail ", e);
                }
            }
        });


    }


    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
