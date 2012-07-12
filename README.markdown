**¸ÅÊö**  
Ó¦ÓÃÍ¨¹ılog4jÊä³öÈÕÖ¾µ½mongodbÊı¾İ¿âÖĞ£¬ÉÁµç¹·¶¨Ê±ÔËĞĞ½Å±¾·ÖÎöÈÕÖ¾£¬Éú³É¼à¿ØÇúÏßºÍ¸æ¾¯¡£Ö÷ÒªÓÅµãÊÇ²»Ó°ÏìÒµÎñ´úÂë£¬Ö»Ğè¼ÓÈë¼¸¸öjar°üºÍĞŞ¸Älog4jÅäÖÃÎÄ¼ş¾ÍÄÜ½ÓÈë¡£
## 
 1. °²×°
* °²×°[mongodb](http://www.mongodb.org/downloads),½âÑ¹³öÀ´£¬ÅäÖÃÊı¾İ±£´æÂ·¾¶£¬¼´¿ÉÔËĞĞ 
* ÏÂÔØ[flash-dog-server](https://github.com/flash-dog/flash-dog/downloads)
* ĞŞ¸Äflash-dog-serverÎÄ¼ş¼ĞÏÂµÄlog4j.propertiesÎÄ¼ş£¬ĞŞ¸Älog4j.appender.MongoDBµÄhostnameºÍportÖ¸ÏòÄã²¿ÊğµÄmongodbµØÖ·ºÍ¶Ë¿Ú£º  
    log4j.appender.MongoDB.hostname=172.16.3.47  
    log4j.appender.MongoDB.port=27017  
* ĞŞ¸Äconf/develop/app.properties£¬Ò²Ö¸Ïò¸Õ²Å²¿ÊğµÄmongodb  
    mongo.uri=mongodb://172.16.3.47:27017/monitor_test  
* ½øÈëbinÏÂ£¬ÔËĞĞstart.sh develop»òÕßstart-dev.bat  
 ÔÚä¯ÀÀÆ÷Àï´ò¿ªhttp://localhost:8080/flash-dog/projects ÊäÈëÓÃ»§Ãû£ºadmin ÃÜÂë 123456 £¬Äã½«¿´µ½Ò»¸ö½ĞÉÁµç¹·µÄÏîÄ¿£¬ÕâÊÇÒòÎªÉÁµç¹·Ò²»á¼à¿Ø×Ô¼º£¬wow
![screenshot](https://github.com/downloads/flash-dog/flash-dog/image-flash-dog-1.jpg)
 2. °ÑÄã×Ô¼ºµÄÏîÄ¿¼ÓÈëÉÁµç¹·
* ´Ó[flash-dog-api-log4j](https://github.com/flash-dog/flash-dog/downloads)ÏÂÔØ¿Í»§¶ËµÄlib¿â£¬»òÕß´Óflash-dog-serverÎÄ¼ş¼ĞlibÄ¿Â¼ÏÂ¿½±´log4mongo-java ,flash-dog-api-log4j ºÍ mongo-java-driver µ½ÏîÄ¿µÄlib¿âÏÂ£¬×¢Òâ»¹ĞèÒªlog4j 1.2.15ÒÔÉÏµÄ°æ±¾
* ĞŞ¸ÄÄú×Ô¼ºµÄlog4j.propertiesÅäÖÃÎÄ¼ş
* ÖØÆôÄúµÄÏîÄ¿
* ½øÈëhttp://localhost:8080/flash-dog/projects ,ĞÂ½¨ÏîÄ¿£¬Èç¹ûÅäÖÃÕıÈ·£¬Äã½«ÔÚ¡¾ÈÕÖ¾·ÖÎö¡¿À¸Ä¿ÖĞ²éÑ¯µ½ÄúµÄÈÕÖ¾£¬enjoy your self£¡

 3. ÅäÖÃËµÃ÷
* log4j.properties
<pre><code>
log4j.appender.MongoDB=org.log4mongo.AsynMongoDbLayoutAppender
log4j.appender.MongoDB.layout=org.log4mongo.contrib.HostInfoPatternLayout
#pid±íÊ¾½ø³ÌºÅ£¬ipÎªµ±Ç°·şÎñÆ÷ip
log4j.appender.MongoDB.layout.ConversionPattern={"timestamp":"%d","level":"%p","className":"%c","message":"%m","pid":"%V","ip":"%I"}
#ºóÌ¨²åÈëÈÕÖ¾Ê¹ÓÃµÄÏß³ÌÊı
log4j.appender.MongoDB.threadCount=2
#ÊÇ·ñ×Ô¶¯´òÓ¡jvmĞÅÏ¢£¬Èçcpu£¬memory£¬threadcount£¬
log4j.appender.MongoDB.jvmMonitor=true
log4j.appender.MongoDB.databaseName=test
log4j.appender.MongoDB.collectionName=flash_dog_log
log4j.appender.MongoDB.hostname=172.16.3.47
log4j.appender.MongoDB.port=27017 
log4j.rootLogger=info,stdout,logfile,MongoDB
<<<<<<< HEAD
</code></pre>
=======
</code></pre>

 4. Ê¹ÓÃlogback
* ÏÂÔØ°²×° [logback-mongodb](https://github.com/flash-dog/logback-mongodb)
* <pre><code>
*      mvn clean install 
* </code></pre>

*  Ôö¼Óslf4j ºÍ logback µÄÒÀÀµ
<pre><code>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.6.5</version>
		</dependency>

		<!--<dependency>-->
			<!--<groupId>org.slf4j</groupId>-->
			<!--<artifactId>slf4j-log4j12</artifactId>-->
			<!--<version>1.6.0</version>-->
		<!--</dependency>-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.0.6</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.6</version>
        </dependency>

        <dependency>
            <groupId>logback.mongodb</groupId>
            <artifactId>logback.mongodb</artifactId>
            <version>1.0.2</version>
        </dependency>
</code></pre>

*logbackµÄÅäÖÃ
<pre><code>	
	<appender name="MONGO" class="logback.mongodb.MongoDBAppender">
        <connectionSource class="logback.mongodb.MongoDBConnectionSource">
            <uri>mongodb://172.16.3.47:27017</uri>
            <db>ugglog</db>
            <collection>paylog</collection>
        </connectionSource>
    </appender>

    <appender name="ASYNCMONGO" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="MONGO" />
    </appender>

    <logger name="currency" additivity="false" level="warn">
        <appender-ref ref="MONGO"/>
    </logger>
</code></pre>

* ÔÚlogbackÖĞ¿ÉÒÔÊ¹ÓÃMDC¹¦ÄÜÊä³ö¸½¼ÓµÄ×Ö¶Î
<pre><code>	
 		MDC.clear();
        MDC.put("mob", mob.name());
        MDC.put("value", value + "");
        MDC.put("currency", type.name());
        MDC.put("reason", reason);
        MDC.put("saveAtOnce", saveAtOnce + "");
        mongoLogger.info("{}:{} {} {} {} {}", new Object[]{logPre, mob.name(), value, type, reason, saveAtOnce});        
</code></pre>
* ÕâÑùÔÚmongodbµÄcollectionÖĞ±£´æµÄscheme»áÔö¼ÓÕâĞ©×Ö¶Î:
<pre><code>	
   {
      mob: '',
	  value: '',
      currency: '',
      reason: '',
      saveAtOnce: '' 
   }
</code></pre>






>>>>>>> å¢åŠ logbackçš„ä½¿ç”¨è¯´æ˜
