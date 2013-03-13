package org.log4mongo;

import com.mongodb.DBObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: Hill.Hu
 * 有众多问题，不再维护
 * @see AsynMongoURILayoutAppender
 * @deprecated
 */
public class AsynMongoDbAppender extends MongoDbAppender {
    private static ThreadPoolExecutor executorService;
    /**
     * 后台写日志的线程个数
     */
    private int threadCount = 1;

    private LinkedBlockingQueue<Runnable> workQueue;
    private int maxWorkSize = 1000;

    public void activateOptions() {
        super.activateOptions();

        workQueue = new LinkedBlockingQueue<Runnable>(2*maxWorkSize);
        executorService = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                workQueue);

    }

    @Override
    public void append(final DBObject bson) {
        if (workQueue.size() < maxWorkSize) {
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
    }

    private void _append(final DBObject bson) {
        super.append(bson);
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setMaxWorkSize(int maxWorkSize) {
        this.maxWorkSize = maxWorkSize;
    }
    
}
