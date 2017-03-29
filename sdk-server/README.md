# SDK Server Sample

一个渠道登录、支付代理通知服务。

## 参考资料
* [AnySDK/Sample_Server
](https://github.com/AnySDK/Sample_Server/tree/master/ServerDemo_Java)


## Architecture

### 应用层&容器层 `App.java`/`pom.xml` 
* `pom.xml` 编译逻辑，如单jar打包
* `App.java` 根据启动参数运行服务内容；同时也是程序的容器层，存放业务参数、处理实例、服务状态等信息；

### 封装好的的 Netty 服务层 `service.*`
* `service.Netty*.java` 封装 Netty HTTP 服务
* `service.Facade*.java` FacadeContext 封装了 Netty 到业务的接口；FacadeBusiness 是业务请求路由逻辑，完全独立于 Netty
* `service.TimeLogger.java` 服务性能日志

### 业务处理层 `adapter.*`
业务始于特定的 HTTP 请求，在`service.FacadeBusiness.process()` 中识别出具体的处理接口和初试参数；

	1. 首先，根据 URI 定义规则(https://<url>/api/<Method>/cc/gg..)，解析出请求方法
	2. 然后，根据方法不同，解析出当前请求的初试参数，如game_id和channel_name等
	3. 获取处理当前请求的业务处理类：`Validator v = ValidatorFactory.getValidator(game, channel)`
	4. 最后，调用处理类上的处理方法，完成请求

业务处理类 `ValidatorFactory`

	根据channel_name来获取实现特定渠道适配逻辑的类，根据game_id加载特定的参数

渠道适配逻辑 `Validator`

	- Validator_XX 之 XX渠道 login token
	
		public static void check_login_token(AgentToken token) {
			Validator v = ValidatorFactory.getValidator(token.appid, token.channelId);
			v.check_token(token);
			logger.debug("response\n" + token.toJSONString());
		}

## Getting Started

Tools used :

1. Maven 3.2.5
1. JDK 1.7.0


### Package It
	➜  sdk-server git:(master) ✗  mvn clean package -DskipTests

### Review It
	➜  sdk-server git:(master) ✗ jar tf sdk-server.jar

### Run It
	➜  sdk-server git:(master) ✗ java -jar target/sdk-server.jar

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
URL: https://<url>/api/LoginAuth/
Method: HTTP POST

输入参数: JSON String: {"appid":"<game_id>", "channelId":"<channel_name>", "channelData":..}

返回结果: JSON String: {"certified":true|false, "channel":"feiliu|uc|360|..", ""userId":"<channel user id>",..}


	➜  sdk-server git:(master) ✗ curl -X POST --data-binary @conf/login.json http://localhost:8000/api/123/LoginAuth/
	{"match":true,"sign":"E1ILPK5vIH+X0UVGS3gRd6+nST3CjJDl6GKt6tEvRUwOBoG9UHo3t9ESQVvKhnISa0dDEFaX6TOkLjhvR3hzUD2zqgA5UlBHqJ9Jys9mLNN5TBt9C25XkQpQ8mrvOvRcA7zyYwzbSdJiCz4zda4zoknehWWLQjI2ZovqOwGJgoXxiOCywUmm1Zu3RJ4kYKboBifPRA1u+KTJ/hSCaawTPFTv3MwUvmM8WAjpy9VRuXEpK137ADM/69Y1Jna8p9SqoUE/sB2jVvWsgj9+4sWCWfiH08IaKuc8gkCjl0zvwGUeDDOYVZP/RH7HlrRSliOc0kM3HfshjGAVaftVZWG6Kg==","timestamp":"1489051858","userId":"1067335"}% 

### 订单签名接口(SDK-Agent -> SDK-Agent-Server)
完整流程: 
URL: https://<url>/api/SignOrder/
Method: HTTP POST

输入参数: JSON String: {"appid":"<game_id>", "channelId":"<channel_name>", "channelData":..}

返回结果: JSON String: {"certified":true|false, "channel":"feiliu|uc|360|..", ""userId":"<channel user id>",..}

### 支付统计接口(Channel -> SDK-Server -> Game-Server)
SDK-Server URL: https://<url>/api/PayNotify/<channel_name>/<game_id>/

Game-Server URL: Game_Server_URL

传入参数: JSON String: {}

➜  sdk-server git:(master) ✗ curl -X POST --data-binary @conf/pay.json http://localhost:8000/api/PayNotify/cc/gg


## 服务部署

	exec java -cp . -jar sdk-server.jar -server

### sdk-agent.ini(supervisord)

	[root@localhost /]# vi /etc/supervisord/sdk-agent.ini
	
	[program:sdk-agent]
	command=/home/boss/sdk-server/run.sh
	process_name=%(program_name)s
	directory=/home/boss/sdk-server/
	startsecs=1
	;stopsignal=QUIT
	stopwaitsecs=5
	user=boss
	redirect_stderr=true
	stdout_logfile=/home/boss/sdk-server/%(program_name)s.log