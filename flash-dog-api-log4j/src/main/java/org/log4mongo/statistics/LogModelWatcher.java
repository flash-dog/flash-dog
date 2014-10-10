package org.log4mongo.statistics;

import com.mongodb.*;
import org.log4mongo.model.LogModel;
import org.log4mongo.mongo.MongoDBClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhengbo.zb on 2014/10/10.
 */
public class LogModelWatcher {
    public static String LOGMODEL_COLLECTION = "flash_dog_log_model";
    public static ConcurrentHashMap<String,LogModel> logModels = new ConcurrentHashMap();
    static Timer timer = new Timer();
    public static void startWatch(final DB db){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DBCollection dbCollection = db.getCollection(LOGMODEL_COLLECTION);
                DBCursor cursor =dbCollection.find();
                boolean notchanged = true;
                Map<String,LogModel> tempmap = new HashMap<String, LogModel>();
                while (cursor.hasNext()) {
                    DBObject dbObject = cursor.next();
                    LogModel logModel = new LogModel(dbObject);
                    String id = logModel.get_id();
                    tempmap.put(id,logModel);
                    notchanged = notchanged&&(logModels.containsKey(id));
                }
                if(!notchanged){
                    logModels.clear();
                    logModels.putAll(tempmap);
                }
            }
        },0,10000);
    }

    public static Map messageFormate(String logname,String message){
        Iterator<String> iterator = logModels.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            LogModel logModel = logModels.get(key);
            Map result = logModel.formatMessage(logname,message);
            if(result!=null)
                return result;
        }
        return null;
    }
}
