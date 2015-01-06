package org.flashdog.mock;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author hushan
 */
@Component
public class MockLogFile {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MockLogFile.class);
    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        if(logger.isDebugEnabled()){
            Thread dog = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if(Math.random()>0.7){
                            logger.error("mock a err message ");
                        } else{
                            logger.debug("mock a debug message ");
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            dog.start();
        }

    }
}
