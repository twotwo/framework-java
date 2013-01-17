Netty Server Guide
=====================
转载请注明：**Powered by li3huo.com**  
技术交流请关注新浪微博 @li3huo

# Netty Server Framework

## What is Netty?

Netty是一个异步的事件驱动网络应用框架，具有高性能、高扩展性等特性。  
Netty提供了统一的底层协议接口，使得开发者从底层的网络协议（比如TCP/IP、UDP）中解脱出来。  
就使用来说，开发者只要参考 Netty提供的若干例子和它的指南文档，
就可以放手开发基于Netty的服务端程序了。

Netty提供的功能包括：  
1.优秀的核心设计：
 * 抛弃jdk自实现的buffer，减少复制所带来的消耗；
 * 庞大而清晰的channel体系，灵活支持NIO/OIO及TCP/UDP多维组合；
 * 基于事件的过程流转以及完整的网络事件响应与扩展；

2.丰富的协议支持：
 * HTTP/WebSocket
 * SSL/TLS
 * Google Protobuf
 * zlib/gzip
 * Large File Transfer
 * RTSP
 * Thrift[注1]

3.丰富的example，帮助开发者快速上手和理解

<img src#"https://netty.io/download/Main/WebHome/architecture.png" >

[netty on github](https://github.com/netty/netty)


## 应用开发框架介绍

### 代码结构
* com.li3huo.netty.DemoServer
 - Daemon
* com.li3huo.netty.service.ServiceContext
 - 服务上下文
 - 目前封装了NioServerSocketChannelFactory对象和服务计数器
* com.li3huo.netty.service.HttpRequestHandler
 - 基于HTTP请求的处理器，框架的处理器基类
* com.li3huo.netty.service.ConsoleHandler
 - 后台管理处理器
 - 实现了服务状态查看/服务关闭功能

### 应用maven-assembly-plugin加强了package功能，实现了保持脚本执行属性和tar.gz打包

### 服务启动和关闭
* 执行mvn package获得发布的应用
* 放到*nix环境下，调用bin/netty.sh

## 其他参考

* [The Netty Project 3.x User Guide](http://static.netty.io/3.6/guide/)
* [注1](https://github.com/facebook/nifty/): Nifty is an implementation of Thrift clients and servers on Netty, written by facebook

## 后续计划

* 继续加强ConsoleServer的功能：结合JMX/提供业务调用计数接口
