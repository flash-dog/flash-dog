package org.log4mongo.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.apache.log4j.Logger;
import org.log4mongo.statistics.LogModelWatcher;

/**
 * Created by zhengbo.zb on 2014/10/10.
 */
public class MongoDBClient {
    static Logger logger = Logger.getLogger(MongoDBClient.class);
    //私有的默认构造子
    private MongoDBClient() {}
    //注意，这里没有final
    private static MongoDBClient single=null;
    private static DB db = null;

    public DB getMongoDB(){
        return MongoDBClient.db;
    }

    public Mongo getMongo(){
        if(MongoDBClient.db==null) return null;
        return MongoDBClient.db.getMongo();
    }

    //静态工厂方法
    public  static MongoDBClient getInstance() {
        if (single == null) {
            single = new MongoDBClient();
        }
        return single;
    }

    //静态工厂方法
    public synchronized  static void init(DB db) {
        single.db = db;
        LogModelWatcher.startWatch(db);
    }
}
