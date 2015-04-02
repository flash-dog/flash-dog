**概述**  
闪电狗监控(flash-dog)起源于杭州斯凯网络科技有限公司一个真实项目，主要优点是轻巧快捷，非侵入式，不影响业务代码，只需加入几个jar包和修改log4j配置文件，就能监控任意指标，如CPU，内存、线程，游戏收入，在线人数等等。2012年开源后，在游戏、支付、教育等多种互联网公司广泛应用。
在线demo ，http://115.28.11.12:8080/flash-dog/   用户 admin  密码 123456  
QQ群：256733302

详细图文教程 
* [安装部署](https://github.com/flash-dog/flash-dog/wiki/1%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2)
* [应用接入](https://github.com/flash-dog/flash-dog/wiki/2.%E5%BA%94%E7%94%A8%E6%8E%A5%E5%85%A5)
* [代理模式](https://github.com/flash-dog/flash-dog/wiki/%E4%BB%A3%E7%90%86%E6%A8%A1%E5%BC%8F)
* [logback扩展](https://github.com/flash-dog/flash-dog/wiki/%E4%BB%A3%E7%90%86%E6%A8%A1%E5%BC%8F)


#常见问题 
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
 
 
