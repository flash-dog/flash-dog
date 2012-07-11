package org.log4mongo;

import com.mongodb.DBObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: Hill.Hu
* @deprecated
 * @see AsynMongoDbLayoutAppender
 */
public class AsynMongoDbAppender extends MongoDbAppender {
    private static ExecutorService executorService;
    /**
     * 后台写日志的线程个数
     */
    private int threadCount=2;

    public void activateOptions() {
        super.activateOptions();

        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void append(final DBObject bson) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    _append(bson);
                } catch (Exception e) {
                    //ingore errors
                }
            }
        });

    }

    private void _append(final DBObject bson) {
        super.append(bson);
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
