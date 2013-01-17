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
package com.skymobi.monitor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
* @author hill.hu
 *
 * mongodb的脚本任务执行器
 */
@SuppressWarnings("unchecked")
public class Task implements Callable, IdentifyObject {
    private static Logger logger = LoggerFactory.getLogger(Task.class);

    public final static int FINE = 0;
    public final static int WARN = 1;
    public final static int ERROR = 2;

    private String script, name;
    private int interval = 5 * 60;
    private int timeout = 20;
    /**
     * 计划执行周期
     */
    private String cron = "5 * * * * *";

    @Override
    public Integer call() {
        try {

            logger.debug("run mongo script = {}", script);
//            CommandResult result = mongoDb.runCmd(script);
//            logger.debug("mongo task response {}", result);
            return FINE;
        } catch (Exception e) {
            logger.error("run mongo cmd error", e);
            return ERROR;
        }
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    public int getInterval() {
        return this.interval;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public String toString() {
        return "Task{" +
        		", name='" + name + '\'' +
        		", cron='" + cron + '\'' +
                "script='" + script + '\'' +
                ", interval=" + interval +
                ", timeout=" + timeout +
                '}';
    }
}
