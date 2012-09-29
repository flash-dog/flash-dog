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

import com.mongodb.DBCursor;
import com.skymobi.monitor.model.LogQuery;
import com.skymobi.monitor.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * 日志分析服务
 *
 * @author Hill.Hu
 */
@Service
public class LogsService {
    private static Logger logger = LoggerFactory.getLogger(LogsService.class);

    @Resource
    ProjectService projectService;
    @Resource
    TaskService taskService;
    private int max = 100;


    public DBCursor findLogs(String projectName, LogQuery logQuery) throws ParseException {
        return findLogs(projectName, logQuery, max);
    }

    public DBCursor findLogs(String projectName, LogQuery logQuery, int max) throws ParseException {
        Project project = projectService.findProject(projectName);
        MongoTemplate template = project.fetchMongoTemplate();

        Query query = new BasicQuery(logQuery.toQuery());
        query.limit(max);

        query.sort().on("timestamp", Order.DESCENDING);
        logger.debug("find logs from {}  by query {} by sort {}", new Object[]{project.getLogCollection(), query.getQueryObject(), query.getSortObject()});
        DBCursor cursor = template.getCollection(project.getLogCollection()).find(query.getQueryObject()).sort(query.getSortObject()).limit(max);
        return cursor;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
