package org.log4mongo;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.log4mongo.contrib.JvmMonitor;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Author: Hill.Hu
 * 异步可定义的写入日志方式
 * mongodb的配置过于死板，强烈建议使用MongoDbPatternLayoutAppender
 * @see AsynMongoURILayoutAppender
 * @deprecated
 * @see MongoDbPatternLayoutAppender
 */
public class AsynMongoDbLayoutAppender extends MongoDbPatternLayoutAppender {
    private static ExecutorService executorService;
    /**
     * 后台写日志的线程个数
     */
    private int threadCount=2;
    private String jvmMonitor     = "false";
    private String jvmMonitorPeriodSeconds = "60";
    private LinkedBlockingQueue<Runnable> workQueue;
    private int maxWorkSize = 1000;
    public void activateOptions() {
        super.activateOptions();
        initJvmMonitor();
        initThreadPoolExecutor();
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
    @Override
    protected void append(final LoggingEvent loggingEvent) {
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


    private void _append(final LoggingEvent loggingEvent) {
        if (isInitialized()) {
            DBObject bson = null;
            String json = layout.format(loggingEvent);

            if (json.length() > 0) {
                Object obj = JSON.parse(json);
                if (obj instanceof DBObject) {
                    bson = (DBObject) obj;
                }
            }

            if (bson != null) {
                try {
                    //由于timestamp被转成了string，所以重新写入时间
                    bson.put("timestamp",new Date());
                    getCollection().insert(bson);
                } catch (MongoException e) {
                    errorHandler.error("Failed to insert document to MongoDB",
                            e, ErrorCode.WRITE_FAILURE);
                }
            }
        }
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setJvmMonitor(String jvmMonitor) {
        this.jvmMonitor = jvmMonitor;
    }

    public void setJvmMonitorPeriodSeconds(String jvmMonitorPeriodSeconds) {
        this.jvmMonitorPeriodSeconds = jvmMonitorPeriodSeconds;
    }

    public void setMaxWorkSize(int maxWorkSize) {
        this.maxWorkSize = maxWorkSize;
    }
}
