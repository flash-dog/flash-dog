package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.time.DateUtils;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * author: Hill.Hu
 */
@SuppressWarnings("unchecked")
public class HttpAlertNotifierTest {
    HttpAlertNotifier notifier = new HttpAlertNotifier();
    private Project project;
    private Alert alert = new Alert();
    private ProjectService projectService;
    private String template;
     private VelocityEngine velocityEngine;
    @Before
    public void setUp() throws Exception {
        project = new Project();
        alert = new Alert();
        alert.setProjectName("flash-dog");
        projectService = mock(ProjectService.class);
        when(projectService.findProject(alert.getProjectName())).thenReturn(project);
        notifier.setProjectService(projectService);
        template = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
                "<message>\n" +
                "<monitoringName>$title</monitoringName>\n" +
                "<monitoringTime>$datetool.format('yyyy-MM-dd hh:mm:ss',$createTime)</monitoringTime>\n" +
                "<monitoringInfo>$esc.xml($content)</monitoringInfo>\n" +
                "<monitoringDemo></monitoringDemo>\n" +
                "<noticeNameOne>$noticeNameOne</ noticeNameOne>\n" +
                "<noticePhonenumOne>13675889987</ noticePhonenumOne>\n" +
                "<noticeEmailOne>iven.tao@sky-mobi.com</noticeEmailOne>\n" +
                "</message>\n";
        velocityEngine=new VelocityEngine();

        velocityEngine.setExtendedProperties(new ExtendedProperties());
        notifier.setVelocityEngine(velocityEngine);
    }

    @Test
    public void test_notify() throws Exception {

        alert.setContent("something happened ,please call hill.hu");
        notifier.setHttpClient(new HttpClient());

        project.getProperties().put(HttpAlertNotifier.HTTP_NOTIFY_CONFIG_URL, "http://www.baidu.com");
        project.getProperties().put(HttpAlertNotifier.HTTP_NOTIFY_CONFIG_TEMPLATE, template);
        project.getProperties().put(HttpAlertNotifier.HTTP_NOTIFY_CONFIG_PROPERTIES,"noticeNameOne=陶国峰\n");
        notifier.notify(alert);
    }

    @Test
    public void test_render_template() throws Exception {

        Map map = new HashMap();
        map.put("content", "异常数据次数:当前值=9.0 > 阀值3.0");
        map.put("title", "游戏基地数据采集完整性监控");
        map.put("createTime", DateUtils.parseDate("2012-08-20 18:22:20",new String[]{"yyyy-MM-dd hh:mm:ss"}));
        map.put("noticeNameOne","陶国峰");

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
                "<message>\n" +
                "<monitoringName>游戏基地数据采集完整性监控</monitoringName>\n" +
                "<monitoringTime>2012-08-20 06:22:20</monitoringTime>\n" +
                "<monitoringInfo>&#24322;&#24120;&#25968;&#25454;&#27425;&#25968;:&#24403;&#21069;&#20540;=9.0 &gt; &#38400;&#20540;3.0</monitoringInfo>\n" +
                "<monitoringDemo></monitoringDemo>\n" +
                "<noticeNameOne>陶国峰</ noticeNameOne>\n" +
                "<noticePhonenumOne>13675889987</ noticePhonenumOne>\n" +
                "<noticeEmailOne>iven.tao@sky-mobi.com</noticeEmailOne>\n" +
                "</message>\n", notifier.renderTemplate(template, map));
    }
}
