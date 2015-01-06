package org.flashdog.agent;

import org.apache.commons.io.input.Tailer;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * @author hill.hu
 */
public class DogMain {
      public  static  void main(String[] args) throws IOException {
          FileSystemXmlApplicationContext applicationContext=new FileSystemXmlApplicationContext("classpath:conf/spring.xml");
          LogFileTailerListener listener = applicationContext.getBean(LogFileTailerListener.class);

          File file = new File(listener.getFileName());
          Tailer tailer = Tailer.create(file,listener,1000,true,true);

      }
}
