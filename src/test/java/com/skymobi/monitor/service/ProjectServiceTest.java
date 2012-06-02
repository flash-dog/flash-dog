package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Project;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Hill.Hu
 */
@ContextConfiguration(locations = {"classpath:spring/mongo-config.xml","classpath:/spring/email-notice.xml"})
public class ProjectServiceTest   extends AbstractJUnit38SpringContextTests {
    @Resource
    ProjectService projectService;


    @Override
    public void setUp() throws Exception {

        projectService.setCollectionName("project");
    }

    public void test_find() throws Exception {
        List<Project> projectList = projectService.findProjects();

        assertTrue(projectList!=null);

    }

    public void test_find_one() throws Exception {
        Project project=new Project();
        project.setName("netsms");

        projectService.saveProject(project);

        Project dbProject = projectService.findProject("netsms");
        assertEquals("netsms", dbProject.getName());

    }

    public void test_save() throws Exception {
        Project project = new Project();
        project.setName("test_project_db");

        projectService.saveProject(project);
        assertNotNull(projectService.findProject("test_project_db"));
        int count = projectService.findProjects().size();
        project.setAlias("测试");
        projectService.saveProject(project);
        assertNotNull(projectService.findProject("test_project_db"));
        assertEquals(count, projectService.findProjects().size());
    }
}
