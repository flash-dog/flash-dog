package com.skymobi.monitor.util;

import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import com.skymobi.monitor.service.ProjectService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * author: Hill.Hu
 */
@ContextConfiguration(locations = {"classpath:spring/env-config.xml", "classpath:spring/services-config.xml", "classpath:/spring/email-notice.xml"})
public class ProjectCreatorTest extends AbstractJUnit4SpringContextTests {
    public static final String SCRIPT = "   //统计错误日志占比\n" +
            "                            err=db.$project.logCollection.find({'level':'ERROR',timestamp:{$gt:new Date(new Date - 300000)}}).count()\n" +
            "                            total=db.$project.logCollection.find({timestamp:{$gt:new Date(new Date - 300000)}}).count();\n" +
            "                            if(total==0)total=1;\n" +
            "                            value=err*100/total;\n" +
            "                            db.$project.metricCollection.save({name:'错误日志百分比',value:value,timeStamp:new Date().getTime()});\n" +
            "                            return value;";
    @Resource
    ProjectService projectService;
    @Resource
    ProjectCreator creator;
    Task task;
    private Project project;

    @Before
    public void setUp() throws Exception {

        project=new Project();
        task=new Task();
        task.setScript(SCRIPT);

    }

    @Test
    public void test_create_self() throws Exception {
           creator.createSelf();
    }

    @Test
    public void test_render_task_template() throws Exception {
        project.setLogCollection("flash_dog_log");
        project.setMetricCollection("flash_dog_metrics");
        Task task1 = creator.renderTask(task, project);
        assertEquals(task.getCron(), task1.getCron());
        assertEquals(task.getName(), task1.getName());
        assertEquals(task.getTimeout(), task1.getTimeout());
        assertEquals("   //统计错误日志占比\n" +
                "                            err=db.flash_dog_log.find({'level':'ERROR',timestamp:{$gt:new Date(new Date - 300000)}}).count()\n" +
                "                            total=db.flash_dog_log.find({timestamp:{$gt:new Date(new Date - 300000)}}).count();\n" +
                "                            if(total==0)total=1;\n" +
                "                            value=err*100/total;\n" +
                "                            db.flash_dog_metrics.save({name:'错误日志百分比',value:value,timeStamp:new Date().getTime()});\n" +
                "                            return value;", task1.getScript());

    }
}
