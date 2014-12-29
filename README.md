**概述**  
应用通过log4j输出日志到mongodb数据库中，闪电狗定时运行脚本分析日志，生成监控曲线和告警。主要优点是不影响业务代码，只需加入几个jar包和修改log4j配置文件就能接入。配置一些javascript小脚本，几乎就能监控所有你想监控的信息，如cpu内存，错误日志百分比，每日访问人数，收入等等  
该项目由[杭州斯凯网络](http://www.sky-mobi.com/)开源，目前已经在公司内部支付等关键业务中使用，对创业团队和中小型公司有较大吸引力。目前只支持java的log4j和logback日志输出，通过简单扩展可以支持其他语言。在线demo ，http://115.28.11.12:8080/flash-dog/   用户 admin  密码 123456  
## 
 1. 安装
* 安装[mongodb](http://www.mongodb.org/downloads),解压出来，配置数据保存路径，打开cmd，输入：D:\mongodb\bin\mongod.exe -dbpath=data即可运行（路径修改成实际目录） 
* 下载[flash-dog-server](https://github.com/flash-dog/flash-dog/releases)
* 修改flash-dog-server文件夹下的log4j.properties文件，修改log4j.appender.MongoDB的hostname和port指向你部署的mongodb地址和端口：  
    log4j.appender.MongoDB.hostname=172.16.3.47  
    log4j.appender.MongoDB.port=27017  
* 修改conf/develop/app.properties，也指向刚才部署的mongodb  
    mongo.uri=mongodb://172.16.3.47:27017/monitor_test  
* 进入bin下，运行start.sh develop或者start-dev.bat  
 在浏览器里打开http://localhost:8080/flash-dog 输入用户名：admin 密码 123456 ，你将看到一个叫闪电狗的项目，这是因为闪电狗也会监控自己，wow
![screenshot](https://github.com/flash-dog/flash-dog/blob/master/screenshot/monitor1.jpg?raw=true)
 2. 把你自己的项目加入闪电狗
* 从[flash-dog-api-log4j](https://github.com/flash-dog/flash-dog/downloads)下载客户端的lib库，或者从flash-dog-server文件夹lib目录下拷贝log4mongo-java ,flash-dog-api-log4j 和 mongo-java-driver 到项目的lib库下，注意还需要log4j 1.2.15以上的版本
* 修改您自己的log4j.properties配置文件
* 重启您的项目
* 进入http://localhost:8080/flash-dog/projects ,新建项目，如果配置正确，你将在【日志分析】栏目中查询到您的日志，enjoy your self！

 3. log4j配置说明
* log4j.properties

<pre><code>
log4j.appender.MongoDB=org.log4mongo.AsynMongoURILayoutAppender
log4j.appender.MongoDB.layout=org.log4mongo.contrib.HostInfoPatternLayout
#pid表示进程号，ip为当前服务器ip
log4j.appender.MongoDB.layout.ConversionPattern={"timestamp":"%d","level":"%p","className":"%c","message":"%m","pid":"%V","ip":"%I"}
#mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
log4j.appender.MongoDB.mongoURI=mongodb://mongolog:123456@192.168.173.207:5281,192.168.173.238:5281/test?slaveOk=true
log4j.appender.MongoDB.jvmMonitor=true
log4j.appender.MongoDB.collectionName=flash_dog_log
log4j.rootLogger=info,stdout,logfile,MongoDB
</code></pre>


 4. 监控示例
* 在闪电狗里面新建监控项目后，点击[定时任务]->[新建任务]。假设业务会打印出：
 <pre><code class="java">
  username=jordon pay money=100
</code></pre>
  点击[指标监控]按钮，自动生成脚本如下：
<pre><code class="java">
m=function () { 
     result = this.message.match(".*money=(\\d+)"); 
     if (result) { 
         pricePaied = new NumberLong(result[1]);         
         emit("pricePaied", pricePaied); 
     } 
 }  
 r= function (key, values) { 
     var total = 0; 
     for (var i = 0; i < values.length; i++) { 
         total += values[i]; 
     } 
     return total; 
 }   
 res=db.flash_dog_log.mapReduce(m, r, {out:"flash_dog_log_output", query:{timestamp:{$gt:new Date(new Date - 300000)}}}); 
 pricePaied=db.flash_dog_log_output.findOne({_id:"pricePaied"});
 if(pricePaied) 
    v=pricePaied.value; 
   else 
     v=0;    
 db.flash_dog_metrics.save({name:"5分钟收入",value:v,timeStamp:new Date().getTime()}); 
 return res;   
</code></pre>

  目的为通过脚本扫描最近5分钟日志，通过正则表示提取 this.message.match(".*money=(\\d+)") 金额，加起来即为最近5分钟收入。生成曲线图如下：  
![screenshot](https://github.com/flash-dog/flash-dog/blob/master/screenshot/shouru1.jpg?raw=true)
* 设置告警 当最近5分钟收入少于100的时候发送邮件，凌晨0点到4点的时候除外。
![screenshot](https://github.com/flash-dog/flash-dog/blob/master/screenshot/warning1.jpg?raw=true)
* 闪电狗目前有4个脚本模板，可以边修改，边调试，非常有趣
 5. 常见问题
* 是否可以监控业务   
  可以，默认是监控jvm性能和错误日志，里面提供了脚步模板，稍作修改便能通过分析业务日志进行业务统计和监控  
* 是否需要连接业务数据库    
  不需要，只需要连接mongodb  
* 什么是仓库模式  
   mongdob的collection的一种模式，仓库模式，有固定大小，空间会重复利用，如设置成1G，数据超过1G，新数据会替老数据，非仓库模式会持续增加，所以对日志表，强烈推荐使用仓库模式。  
* 为什么添加的新项目没有看到图表  
   大部分问题都是数据库链接问题，可在项目设置和日志里面查看数据链接是否正常。遵守命名约定可以减少低级错误
* 如何输出自定义变量  
   通过log4j的MDC实现，MDC内部使用了类似map的机制来存储信息，相对应的方法，MDC.put(key,value);在配置PatternLayout的时候使用：%x{key}来输出对应的value,如跟踪用户请求的所有日志： 
   java代码：
    <pre><code class="java"> 
	MDC.put("UUID", userId); 
    </code></pre>
  log4j配置： 
    <pre><code class="java">  
  log4j.appender.MongoDB.layout.ConversionPattern={"timestamp":"%d","level":"%p","className":"%c","message":"%m","pid":"%V","ip":"%I",uuid:"%X{UUID}"}
    </code></pre>   
* 如何监控tomcat访问日志  
  1.先配置将tomcat日志输出到log4j，下载tomcat-juli.jar到tomcat/bin,覆盖原来的jar，下载tomcat-util.jar到tomcat/lib，复制log4mongo-java ,flash-dog-api-log4j 和 mongo-java-driver ，log4j.properties到lib  
  2.修改tomcat/conf/server.xml中access-log部分：  className="com.skymobi.tomcat.Log4JAccessLogValve"   
  3.重启tomcat，在logs/catalina.log 可以看到访问日志    

**扩展**   
  如果您的项目没有使用log4j，也可以使用logback.
* 下载安装 [logback-mongodb](https://github.com/flash-dog/logback-mongodb)
        <pre><code>
            mvn clean install
        </code></pre>

* 增加slf4j 和 logback 的依赖

		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.6.5</version>
		</dependency>

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


* logback的配置

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


* 在logback中可以使用MDC功能输出附加的字段
        <pre><code class="java">
                MDC.clear();
                MDC.put("mob", mob.name());
                MDC.put("value", value + "");
                MDC.put("currency", type.name());
                MDC.put("reason", reason);
                MDC.put("saveAtOnce", saveAtOnce + "");
                mongoLogger.info("{}:{} {} {} {} {}", new Object[]{logPre, mob.name(), value, type, reason, saveAtOnce});
        </code></pre>

* 这样在mongodb的collection中保存的scheme会增加这些字段:
        <pre><code>
           {
              mob: '',
              value: '',
              currency: '',
              reason: '',
              saveAtOnce: ''
           }
        </code></pre>
