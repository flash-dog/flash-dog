package org.log4mongo.contrib;

import com.sun.management.OperatingSystemMXBean;
import org.apache.log4j.Logger;
import sun.management.ManagementFactory;

import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: Hill.Hu
 */
public class JvmMonitor {
	private static JvmMonitor uniqueInstance = null;
    private static Logger logger = Logger.getLogger(JvmMonitor.class);
    private long lastProcessCpuTime = 0;
    private long lastUptime = 0;
    public static final int DEFAULT_REFRESH_SECONDS = 60;

    public synchronized static  JvmMonitor getInstance(int periodSeconds){
    	if(uniqueInstance==null)
    		uniqueInstance=new JvmMonitor(periodSeconds);
    	return uniqueInstance;
    }
    
    public synchronized static  JvmMonitor getInstance(){
    	if(uniqueInstance==null)
    		uniqueInstance=new JvmMonitor();
    	return uniqueInstance;
    }
    
    private JvmMonitor() {
        this(DEFAULT_REFRESH_SECONDS);
    }

    private JvmMonitor(int periodSeconds) {

        logger.info("jvm monitor start  ...");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                record();
            }
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }

    public void record() {
        String message = "memoryUsed=" + getMemoryUsed() + "k "
                + " cpuUsed=" + getCpu() + " threadCount=" + getThreadCount();
        logger.info(message);


    }

    protected int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    protected long getMemoryUsed() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024);
    }

    protected double getCpu() {
        OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        RuntimeMXBean runbean = java.lang.management.ManagementFactory
                .getRuntimeMXBean();
        long uptime = runbean.getUptime();
        long processCpuTime = osbean.getProcessCpuTime();
        //cpu count
        int processors = osbean.getAvailableProcessors();
        //uptime in milliseconds ,and    processCpuTime in nao seconds
        double cpu = (processCpuTime - lastProcessCpuTime) / ((uptime - lastUptime) * 10000f * processors);
        lastProcessCpuTime = processCpuTime;
        lastUptime = uptime;
        return (int) cpu;  //
    }

}
