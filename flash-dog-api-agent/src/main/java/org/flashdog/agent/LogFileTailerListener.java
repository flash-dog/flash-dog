package org.flashdog.agent;

import com.mongodb.*;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hushan
 */
@Component
public class LogFileTailerListener extends TailerListenerAdapter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LogFileTailerListener.class);
    @Value("${input.patternTxt}")
    private String patternTxt = "";
    @Value("${input.dateFormat}")
    private String dateFormat = "";
    @Value("${input.fields}")
    private String fields;
    private Pattern pattern;
    @Value("${mongo.uri}")
    private String mongoURI;
    @Value("${mongo.collection}")
    private String collectionName = "log_file";

    private Mongo mongo = null;
    private DBCollection collection = null;
    @Value("${input.file}")
    private String fileName;
    private AtomicLong counter = new AtomicLong();
    /**
     * 后台写日志的线程个数
     */
    private int threadCount = 2;
    private LinkedBlockingQueue<Runnable> workQueue;
    private int maxWorkSize = 1000;
    private static ThreadPoolExecutor executorService;

    @Override
    public void handle(String line) {
        try {

            final DBObject bson = convert(line);
            if (logger.isDebugEnabled()) {
                logger.debug("json= " + bson.toString());
            }

            counter.incrementAndGet();
            if (workQueue.size() < maxWorkSize) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            collection.insert(bson);
                        } catch (Exception e) {
                            //ingore errors
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error("parse line err:", e);
        }

    }

    @Override
    public void fileNotFound() {
        logger.error("file not find [{}]", fileName);
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws MongoException, UnknownHostException {
        if (logger.isDebugEnabled()) {
            testDog();
        }
        Assert.hasLength(patternTxt, "pattern text should not be empty");
        setPatternTxt(patternTxt);
        MongoURI uri = new MongoURI(mongoURI);
        mongo = new Mongo(uri);
        collection = (mongo.getDB(uri.getDatabase()).getCollection(collectionName));

        workQueue = new LinkedBlockingQueue<Runnable>(2 * maxWorkSize);
        executorService = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                workQueue);
    }

    public void setPatternTxt(String patternTxt) {
        this.patternTxt = patternTxt;
        try {
            pattern = Pattern.compile(patternTxt);
        } catch (Exception e) {
            throw new IllegalArgumentException("patternTxt err:" + e.getMessage());
        }

    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public DBObject convert(String line) {
        Matcher matcher = pattern.matcher(line);
        DBObject bson = new BasicDBObject();
        String[] fields = this.fields.split(" ");
        if (matcher.matches()) {
            if (matcher.groupCount() >= fields.length) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String field = fields[i - 1];
                    String group = matcher.group(i);
                    if (field.equalsIgnoreCase("timestamp")) {

                        bson.put(field, toDate(group));
                    } else {
                        bson.put(field, group);
                    }

                }
            } else {
                logger.error("解析结果字段个数不一致:" + this.fields);
            }

        } else {
            logger.error("解析失败:" + line);
        }

        //由于timestamp被转成了string，所以重新写入时间
        bson.put("timestamp", new Date());
        return bson;
    }

    private Date toDate(String group) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.parse(group);
        } catch (ParseException e) {
            logger.error("date convert fail input=[{}] ,format=[{}]", group, dateFormat);
        }
        return new Date();
    }

    @Override
    public void fileRotated() {
        logger.info("file rotated");
    }

    @Override
    public void init(Tailer tailer) {
        super.init(tailer);
    }

    @Override
    public void handle(Exception ex) {
        logger.error("", ex);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void testDog() {
        Thread dog = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    logger.info("total send lines count={} ", counter.get());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dog.start();
    }
}
