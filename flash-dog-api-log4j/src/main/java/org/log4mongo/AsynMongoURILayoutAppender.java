package org.log4mongo;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mongodb.*;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.log4mongo.contrib.JvmMonitor;

import com.mongodb.util.JSON;
import org.log4mongo.mongo.MongoDBClient;
import org.log4mongo.statistics.LogModelWatcher;

/**
 * 通过mongURI参数配置mongdb的参数
 * @author Steven.Zheng
 * 2012-12-31
 */
public class AsynMongoURILayoutAppender extends BsonAppender {
	
    private final static String DEFAULT_MONGO_DB_COLLECTION_NAME = "logevents";
    private final static String STATISTIC_COLLECTION_NAME = "flash_dog_statistics";
    
    private String mongoURI;
    private String collectionName = DEFAULT_MONGO_DB_COLLECTION_NAME;
    private String jvmMonitor     = "false";
    private String jvmMonitorPeriodSeconds = "60";
    
//    private Mongo mongo = null;
    private DBCollection collection;
    private DBCollection statisticCollection;
    private boolean initialized = false;
    
    /**
     * 后台写日志的线程个数
     */
    private int threadCount = 2;
    private LinkedBlockingQueue<Runnable> workQueue;
    private int maxWorkSize = 1000;
    private static ThreadPoolExecutor executorService;

    /**
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout() {
        return (true);
    }
    
    /** 初始化线程池 */
    public void initThreadPoolExecutor(){
        workQueue = new LinkedBlockingQueue<Runnable>(2*maxWorkSize);
        executorService = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                workQueue);
    }
    /**  性能监控初始化*/
    private void initJvmMonitor() {
        if(!jvmMonitor.equals("true")) return;
        if(jvmMonitorPeriodSeconds!=null&&!jvmMonitorPeriodSeconds.equals("")&&jvmMonitorPeriodSeconds.matches("[0-9]*"))
            JvmMonitor.getInstance(Integer.parseInt(jvmMonitorPeriodSeconds));
        else
            JvmMonitor.getInstance();
    }
    /** mongodb初始化*/
    private void initMongodb() throws MongoException, UnknownHostException{

    	 if (MongoDBClient.getInstance().getMongoDB() != null)  close();
         MongoURI uri = new MongoURI(mongoURI);
         Mongo mongo = new Mongo(uri);
         MongoDBClient.getInstance().init(mongo.getDB(uri.getDatabase()));
         setCollection(MongoDBClient.getInstance().getMongoDB().getCollection(collectionName));
        setStatisticCollection(MongoDBClient.getInstance().getMongoDB().getCollection(STATISTIC_COLLECTION_NAME));
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#activateOptions()
     */
    @Override
    public void activateOptions() {
        try {
        	initThreadPoolExecutor();
        	initJvmMonitor();
        	initMongodb();
            initialized = true;
        } catch (Exception e) {
            errorHandler.error("Unexpected exception while initialising MongoDbAppender.", e,
                    ErrorCode.GENERIC_FAILURE);
        }
    }

    public void setStatisticCollection(final DBCollection collection){
        this.statisticCollection = collection;
    }

    public void setCollection(final DBCollection collection) {
        assert collection != null : "collection must not be null.";
        this.collection = collection;
    }
    
    public String getCollectionName() {
    	return collectionName;
    }

    public void close() {
        if (MongoDBClient.getInstance().getMongo() != null) {
            collection = null;
            MongoDBClient.getInstance().getMongo().close();
        }
    }
    public void setCollectionName(final String collectionName) {
        assert collectionName != null : "collection must not be null";
        assert collectionName.trim().length() > 0 : "collection must not be empty or blank";
        this.collectionName = collectionName;
    }
    


    @Override
    protected void append(final LoggingEvent loggingEvent) {
        Map MDCdata = loggingEvent.getProperties();
    	 if (workQueue.size() < maxWorkSize) {
             executorService.execute(new Runnable() {
             @Override
             public void run() {
                 try {
                     _append(loggingEvent);
                 } catch (Exception e) {
                     //ingore errors
                 }
             }
         });
    }
    }

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LOGGER_NAME = "loggerName";
    private static final String KEY_LOGMODEL_ID = "logmodelid";

    private String logForStatistics(final LoggingEvent loggingEvent){
        if (loggingEvent != null) {
            String message = loggingEvent.getMessage().toString();
            String logname = loggingEvent.getLoggerName();
            Map map = LogModelWatcher.messageFormate(logname,message);
            if(map==null)
                return null;
            String logmodelid = (String)map.get("logmodelid");
            DBObject result = new BasicDBObject();
            result.put(KEY_TIMESTAMP, new Date(loggingEvent.getTimeStamp()));
            result.put(KEY_LOGGER_NAME, loggingEvent.getLoggerName());
            result.put(KEY_LOGMODEL_ID,logmodelid);
            result.putAll(map);
            getStatisticCollection().insert(result);
            return logmodelid;
        }else {
            return null;
        }
    }


    
    private void _append(final LoggingEvent loggingEvent) {
        if (isInitialized()) {
            DBObject bson = null;
            String logmodelid = logForStatistics(loggingEvent);

            String json = layout.format(loggingEvent);

            if (json.length() > 0) {
                Object obj = JSON.parse(json);
                if (obj instanceof DBObject) {
                    bson = (DBObject) obj;
                  //将MDC中的属性赋值给mongodb数据对象
                    bson.putAll(loggingEvent.getProperties());
                }
            }

            if (bson != null) {
                try {
                    if(logmodelid!=null){
                        bson.put(KEY_LOGMODEL_ID,logmodelid);
                    }
                    //由于timestamp被转成了string，所以重新写入时间
                    bson.put(KEY_TIMESTAMP,new Date());
                    getCollection().insert(bson);
                } catch (MongoException e) {
                    errorHandler.error("Failed to insert document to MongoDB",
                            e, ErrorCode.WRITE_FAILURE);
                }
            }
        }
    }
    	

    public boolean isInitialized() {
        return initialized;
    }

    protected DBCollection getCollection() {
        return collection;
    }

    protected DBCollection getStatisticCollection() {
        return statisticCollection;
    }

	public String getMongoURI() {
		return mongoURI;
	}

	public void setMongoURI(String mongoURI) {
		assert mongoURI != null : "mongoURI must not be null";
        assert mongoURI.trim().length() > 0 : "mongoURI must not be empty or blank";
		this.mongoURI = mongoURI;
	}

	@Override
	protected void append(DBObject bson) {
	}

	public String getJvmMonitor() {
		return jvmMonitor;
	}

	public void setJvmMonitor(String jvmMonitor) {
		this.jvmMonitor = jvmMonitor;
	}

	public String getJvmMonitorPeriodSeconds() {
		return jvmMonitorPeriodSeconds;
	}

	public void setJvmMonitorPeriodSeconds(String jvmMonitorPeriodSeconds) {
		this.jvmMonitorPeriodSeconds = jvmMonitorPeriodSeconds;
	}
    
}
