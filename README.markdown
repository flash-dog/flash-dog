* 概述
应用通过log4j输出日志到mongodb数据库中，闪电狗定时运行脚本分析日志，生成监控曲线和告警。主要优点是不影响业务代码，只需加入几个jar包和修改log4j配置文件就能接入。

 1. 安装
* 安装[mongodb](http://www.mongodb.org/downloads),解压出来，配置数据保存路径，即可运行 
* 下载[flash-dog-server](http://github.com/mongodb/mongo-java-driver/downloads)
* 修改flash-dog-server文件夹下的log4j.properties文件，修改log4j.appender.MongoDB的hostname和port指向你部署的mongodb地址和端口：  
	log4j.appender.MongoDB.hostname=172.16.3.47  
	log4j.appender.MongoDB.port=27017  
* 修改conf/develop/app.properties，也指向刚才部署的mongodb  
	mongo.uri=mongodb://172.16.3.47:27017/monitor_test  


* 进入bin下，运行start.sh develop或者start-dev.bat  
 在浏览器里打开http://localhost:8080/flash-dog/projects 输入用户名：admin 密码 123456 ，你将看到一个叫闪电狗的项目，这是因为闪电狗会监控自己，wwo  

