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

import com.skymobi.monitor.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hill.Hu
 */
@ContextConfiguration(locations = {"classpath:spring/env-config.xml", "classpath:spring/mongo-config.xml", "classpath:/spring/email-notice.xml"})
public class ProjectServiceTest extends AbstractJUnit4SpringContextTests {
    @Resource
    ProjectService projectService;


    @Before
    public void setUp() throws Exception {

        projectService.setCollectionName("project");
    }

    @Test
    public void test_find() throws Exception {
        List<Project> projectList = projectService.findProjects();

        assertTrue(projectList != null);

    }

    @Test
    public void test_find_one() throws Exception {
        Project project = new Project();
        project.setName("netsms");

        projectService.saveProject(project);

        Project dbProject = projectService.findProject("netsms");
        assertEquals("netsms", dbProject.getName());

    }

    @Test
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
