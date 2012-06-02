package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-8 上午9:29
 * 扫描各个项目发件箱，并发出邮件
 */
public class EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private ProjectService projectService;
    private boolean enable = true;
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private String from;

    public void send(Alert alert) {
        Project project = projectService.findProject(alert.getProjectName());
        if (project == null) {
            logger.error("projectName of  alert is null, alert={}", alert);
            return;
        }
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setText(alert.getContent());
        sm.setSubject(alert.getTitle());
        sm.setFrom(from);
        String mailList = project.getMailList();
        if (mailList != null)
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
                    mailSender.send(msg);
                } catch (MailException e) {
                    logger.error("send msg fail {}", e.getMessage());
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
