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

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Hill.Hu
 */
@SuppressWarnings("unchecked")
public class TaskService {
    private static Logger logger = LoggerFactory.getLogger(TaskService.class);
    private ConcurrentTaskScheduler executor = new ConcurrentTaskScheduler();
    private final static Map<String, ScheduledFuture> futures = Maps.newHashMap();

    public void scheduledTask(final Project project, final Task task) {
        final String projectName = project.getName();
        //先取消老的任务
        removeScheduled(projectName, task);
        String taskKey = getTaskKey(projectName, task);
        ScheduledFuture<?> future = executor.schedule(new Runnable() {
            @Override
            public void run() {
            	long startTime = System.currentTimeMillis();
                try {
                    FutureTask _fuFutureTask = TaskService.this.runScript(task.getScript(), project);
                    _fuFutureTask.get(task.getTimeout(), TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    logger.error("execute task timeout,use  {} (second), project={} , script={}",
                    		new Object[]{(System.currentTimeMillis()-startTime)/1000, projectName,task});
                } catch (Exception e) {
                	logger.error("execute task ERROR,use  {} (second), project={} , script={}",
                    		new Object[]{(System.currentTimeMillis()-startTime)/1000, projectName,task});
                    logger.error("execute task fail", e);
                }
                logger.info("execute task success,use {} (second) , project={} , taskName={}",
                		new Object[]{(System.currentTimeMillis()-startTime)/1000, projectName,task.getName()});
            }
        }, new CronTrigger(task.getCron()));
        logger.info("add a new task {}", taskKey);
        futures.put(taskKey, future);
    }

    public FutureTask<CommandResult> runScript(final String script, final Project project) {
        FutureTask<CommandResult> _fuFutureTask = new FutureTask(new Callable() {

            @Override
            public CommandResult call() throws Exception {

                logger.debug("run mongo script = {}", script);
                CommandResult result = project.fetchMongoTemplate().getDb().doEval(script, new BasicDBObject().append("nolock", true));
                logger.debug("mongo task response {}", result);
                return result;
            }
        });
        executor.submit(_fuFutureTask);
        return _fuFutureTask;
    }

    private String getTaskKey(String projectName, Task task) {
        return projectName + "_" + task.getName();
    }

    public void removeScheduled(String projectName, Task task) {
        String taskKey = getTaskKey(projectName, task);
        if (futures.containsKey(taskKey)) {
            logger.info("remove old task {}", taskKey);
            futures.get(taskKey).cancel(true);
        }

    }

    public void startTasks(Project project) {
        try {
            logger.info("start task of project {}", project.getName());
            for (final Task task : project.getTasks()) {
                logger.debug("schedule task {} ,cron ={}", task.getName(), task.getCron());
                scheduledTask(project, task);
            }
        } catch (Exception e) {
            logger.error("start task of  project fail name={} {}", project.getName(), e);
        }
    }

    public void setExecutor(ConcurrentTaskScheduler executor) {
        this.executor = executor;
    }
}
