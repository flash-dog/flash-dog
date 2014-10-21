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

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.skymobi.monitor.model.LogQuery;
import com.skymobi.monitor.model.Project;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.log4mongo.enums.FieldEnum;
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

    //--todo 目前存储在日志提供者的mongodb中，未来版本统一管理
    private static final String collectionName = "flash_dog_log_model";

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

    public void saveLogModel(String projectName,String logModelId,String relation,String logname,String matchstr,String logModelName){
        if(StringUtils.isBlank(relation)
                ||StringUtils.isBlank(logname)
                ||StringUtils.isBlank(matchstr)
                ||StringUtils.isBlank(logModelName)){
            return;
        }
        Project project = projectService.findProject(projectName);
        MongoTemplate mongoTemplate = project.fetchMongoTemplate();
        if(isModleLogExist(mongoTemplate,logname,matchstr)||
                isModleLogModelNameExist(mongoTemplate,logModelName)){
            return;
        }
        //已存在的日志模型
        DBObject result = new BasicDBObject();
        if(StringUtils.isNotBlank(logModelId)){
            DBObject query  = new BasicDBObject();
            query .put("_id", new ObjectId(logModelId));
            result=mongoTemplate.getCollection(collectionName).findOne(query);
        }

        DBObject relationObj = (DBObject) JSON.parse(relation);
        result.put(FieldEnum.KEY_RELATION.getName(),relationObj);
        result.put(FieldEnum.KEY_LOGGER_NAME.getName(),logname);
        result.put(FieldEnum.KEY_MATCH_STR.getName(),matchstr);
        result.put(FieldEnum.KEY_LOGGER_MODEL_NAME.getName(),logModelName);

        mongoTemplate.getCollection(collectionName).save(result);
    }

    public boolean isModleLogExist(MongoTemplate mongoTemplate,String logname,String matchstr){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("logName",logname);
        dbObject.put("matchStr",matchstr);
        DBObject result=mongoTemplate.getCollection(collectionName).findOne(dbObject);
        return result!=null;
    }

    public boolean isModleLogModelNameExist(MongoTemplate mongoTemplate,String logModelName){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("logModelName",logModelName);
        DBObject result=mongoTemplate.getCollection(collectionName).findOne(dbObject);
        return result!=null;
    }

    public DBObject queryLogModel(String projectName, String logModelId) {
        Project project = projectService.findProject(projectName);
        MongoTemplate mongoTemplate = project.fetchMongoTemplate();
        DBObject query  = new BasicDBObject();
        query .put("_id", new ObjectId(logModelId));
        return mongoTemplate.getCollection(collectionName).findOne(query);
    }
}
