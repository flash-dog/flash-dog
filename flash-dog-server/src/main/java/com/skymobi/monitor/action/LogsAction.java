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
package com.skymobi.monitor.action;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.skymobi.monitor.model.Log;
import com.skymobi.monitor.model.LogQuery;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.service.LogsService;
import com.skymobi.monitor.service.ProjectService;
import com.skymobi.monitor.service.TaskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Hill.Hu, Steven.Zheng
 */
@Controller
public class LogsAction {
    private static Logger logger = LoggerFactory.getLogger(LogsAction.class);

    private Executor executor = Executors.newFixedThreadPool(10);

    @Value("${flashdog.mongWaitSeconds}")
    private int mongWaitSeconds;

    @Resource
    private ProjectService projectService;

    @Resource
    private LogsService logsService;

    @RequestMapping(value = "/projects/{projectName}/logs", method = RequestMethod.GET)
    public String test(ModelMap map, @PathVariable String projectName) {
        Project project = projectService.findProject(projectName);
        map.put("project", project);
        return "logs/show";
    }

    @RequestMapping(value = "/projects/{projectName}/logs/download", method = RequestMethod.GET)
    public void download(final HttpServletResponse response, ModelMap map, @PathVariable String projectName, LogQuery logQuery) throws IOException, ParseException {
        Project project = projectService.findProject(projectName);

        final MongoConverter converter = project.fetchMongoTemplate().getConverter();
        final DBCursor cursor = logsService.findLogs(projectName, logQuery, 100000);
        response.setContentType("file/txt;charset=utf-8");
        response.addHeader("content-disposition", String.format("attachment; filename=%s.txt", java.net.URLEncoder.encode("logs", "UTF-8")));
        response.setStatus(HttpServletResponse.SC_OK);

        while (cursor.hasNext()) {
            Log log = converter.read(Log.class, cursor.next());

            response.getWriter().println(log.toString());

        }
    }

    /**
     * 保存日志模型
     * @param projectName
     */
    @RequestMapping(value = "/projects/{projectName}/logs/submitLogModel", method = RequestMethod.POST)
    @ResponseBody
    public void submitLogModel(@PathVariable String projectName,HttpEntity<Map> httpEntity){
        Map map = httpEntity.getBody();
        String relation = (String)map.get("relation");
        String logname = (String)map.get("logname");
        String matchstr= (String)map.get("matchstr");
        logsService.saveLogModel(projectName,relation,logname,matchstr);
    }

    /**
     * 根据id查询日志模型
     * @param projectName
     * @return
     */
    @RequestMapping(value = "/projects/{projectName}/logs/queryLogModel", method = RequestMethod.POST)
    @ResponseBody
    public DBObject queryLogModel(@PathVariable String projectName,HttpEntity<Map> httpEntity){
        Map map = httpEntity.getBody();
        String logmodelid = (String)map.get("logmodelid");
        return logsService.queryLogModel(projectName,logmodelid);
    }

    @RequestMapping(value = "/projects/{projectName}/logs/list", method = RequestMethod.GET)
    @ResponseBody
    public List loglist( ModelMap map, @PathVariable String projectName, LogQuery logQuery) throws IOException, ParseException {
        Project project = projectService.findProject(projectName);
        final MongoConverter converter = project.fetchMongoTemplate().getConverter();
        final DBCursor cursor = logsService.findLogs(projectName, logQuery);

        @SuppressWarnings("unchecked")
        FutureTask<List> task = new FutureTask(new Callable<List>() {
            @Override
            public List call() throws Exception {
                List<Log> logs = new ArrayList();
                long startTime = System.currentTimeMillis();
                //遍历游标，最长不能超过20秒
                while (cursor.hasNext()) {
                    Log log = converter.read(Log.class, cursor.next());
                    logs.add(log);
                    long current = System.currentTimeMillis();
                    if ((current - startTime) / 1000 >= mongWaitSeconds) break;
                }
                return logs;
            }
        });
        executor.execute(task);
        List l = null;
        try {
            l = task.get(mongWaitSeconds + 5, TimeUnit.SECONDS);
            map.put("logs",l);
        } catch (Exception e) {
            logger.error("查询超时 ", e);
            task.cancel(true);
        }finally {
            cursor.close();
        }
        return l;
    }



}
