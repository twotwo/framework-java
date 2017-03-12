# SDK Server Sample

一个渠道登录、支付代理通知服务。

## 参考资料
* [AnySDK/Sample_Server
](https://github.com/AnySDK/Sample_Server/tree/master/ServerDemo_Java)

## Create Project

Tools used :

1. Maven 3.2.5
1. JDK 1.7.0


### Package It
	➜  mvn_uber_jar git:(master) ✗ mvn clean package

### Review It
	➜  mvn_uber_jar git:(master) ✗ jar tf target/webserver.jar

## Netty SDK Sample(com.li3huo.service)
* [HTTP snoop sample for 4.1](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/snoop)

### Start time server

	➜  sdk-server git:(master) ✗ sudo java -cp src/main/resources -jar target/sdk-server.jar -server

### Access Test

	➜  target git:(master) ✗ curl -X POST --data-binary @sdk-server.jar http://localhost:8000/sadfa
	/sadfa
	{User-Agent=curl/7.51.0, Accept=*/*, Expect=100-continue, Host=localhost:8000, Content-Type=application/x-www-form-urlencoded, Content-Length=3043347}
	Size: 3043347%

### RSA Test

	➜  target git:(master) ✗ ssh-keygen -t rsa -b 4096 -C "admin@li3huo.com" -f  /tmp/key -q -N ""

## Interface

### 登录验证接口(Game-Server -> SDK-Agent-Server)
完整流程: Unity -> SDK-Agent -> Unity -> (Game-Server -> SDK-Agent-Server)
URL: https://<url>/api/<game_id>/LoginAuth/
Method: HTTP POST

输入参数: 透传

返回结果: JSON String: {"match":true|false, "channel":"feiliu|uc|360|..", "userid":"<channel user id>",..}


	➜  sdk-server git:(master) ✗ curl -X POST --data-binary @conf/login.json http://localhost:8000/api/123/LoginAuth/
	{"match":true,"sign":"E1ILPK5vIH+X0UVGS3gRd6+nST3CjJDl6GKt6tEvRUwOBoG9UHo3t9ESQVvKhnISa0dDEFaX6TOkLjhvR3hzUD2zqgA5UlBHqJ9Jys9mLNN5TBt9C25XkQpQ8mrvOvRcA7zyYwzbSdJiCz4zda4zoknehWWLQjI2ZovqOwGJgoXxiOCywUmm1Zu3RJ4kYKboBifPRA1u+KTJ/hSCaawTPFTv3MwUvmM8WAjpy9VRuXEpK137ADM/69Y1Jna8p9SqoUE/sB2jVvWsgj9+4sWCWfiH08IaKuc8gkCjl0zvwGUeDDOYVZP/RH7HlrRSliOc0kM3HfshjGAVaftVZWG6Kg==","timestamp":"1489051858","userId":"1067335"}% 

### 支付统计接口(Channel -> SDK-Server -> Game-Server)
SDK-Server URL: https://<url>/api/<game_id>/PayNotify/<channel_name>/

Game-Server URL: Game_Server_URL

传入参数: JSON String: {}

➜  sdk-server git:(master) ✗ curl -X POST --data-binary @conf/pay.json http://localhost:8000/api/123/PayNotify/cc/
