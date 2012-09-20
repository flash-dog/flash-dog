package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Status;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hill.Hu
 */
public class ProjectStatusServiceTest extends TestCase {
    ProjectStatusService projectStatusService=new ProjectStatusService();
    private Alert alert;
    private ProjectService projectService;
    private Project project;

    @Override
    public void setUp() throws Exception {
        alert=new Alert();
        alert.setProjectName("projectName");
        projectService=mock(ProjectService.class);
        projectStatusService.setProjectService(projectService);
        project=new Project();
    }

    public void test_change_by_alert() throws Exception {
        when(projectService.findProject(alert.getProjectName())).thenReturn(project);
        project.setStatus(Status.FINE);
        alert.setLevel("FINE");
        projectStatusService.notify(alert);
        assertEquals(Status.FINE,project.getStatus());

        alert.setLevel("WARN");
        projectStatusService.notify(alert);
        assertEquals(Status.WARN,project.getStatus());

        alert.setLevel("ERROR");
        projectStatusService.notify(alert);
        assertEquals(Status.ERROR,project.getStatus());

        alert.setLevel("WARN");
        projectStatusService.notify(alert);
        assertEquals(Status.ERROR,project.getStatus());
    }
}
