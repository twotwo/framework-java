Demo for buildnumber-maven-plugin
==================================
转载请注明：**Powered by li3huo.com**  
技术交流请关注新浪微博 @li3huo


## 1. 参考资料

### [插件官网](http://www.mojohaus.org/buildnumber-maven-plugin/ "buildnumber-maven-plugin")

### [使用介绍](http://www.mojohaus.org/buildnumber-maven-plugin/usage.html "Usage")

### [stackoverflow](http://stackoverflow.com/questions/14976824/how-can-i-get-jenkins-build-number-svn-revision-number-and-display-it-on-my "帮助")

### [wiki](http://wiki.li3huo.com/Maven "Maven")

## 2. 配置说明

### 创建工程

	➜  framework-java git:(master) ✗  mvn -B archetype:generate \
	  -DarchetypeGroupId=com.li3huo.archetypes \
	  -DarchetypeArtifactId=archetype-simple-project \
	  -DgroupId=com.li3huo.guide.mvn \
	  -DartifactId=mvn_build_number

	➜  framework-java git:(master) ✗ cd mvn_build_number

### 配置pom.xml

创建`<build>`标签，并在`<resources>`添加`<filtering>`；在`<plugins>`中加入`buildnumber-maven-plugin`

[主要参考](https://github.com/mojohaus/mojo-parent/blob/master/pom.xml "scm的github配置")

### 配置build.properties

	➜  mvn_build_number git:(master) ✗ mkdir src/main/resources/assemble
	➜  mvn_build_number git:(master) ✗ cat << EOF >> src/main/resources/assemble/build.properties
	server.vendor		= ${pom.groupId}
	server.version		= ${pom.version}
	server.tag			= ${scmBranch}
	server.buildNumber	= ${buildNumber}
	build_id			= ${env.BUILD_ID}
	EOF

### 编写BuildInfo.java

	➜  mvn_build_number git:(master) ✗ st src/main/java/com/li3huo/guide/mvn/BuildInfo.java

## 3. 使用说明
	
	➜  mvn_build_number git:(master) ✗
	# 本地测试设置参数
	➜  mvn_build_number git:(master) ✗ MAVEN_OPTS=-Dmaven.buildNumber.doCheck=false
	# 模拟Jenkins打包环境
	➜  mvn_build_number git:(master) ✗ export BUILD_NUMBER=22

	➜  mvn_build_number git:(master) ✗ cat target/classes/assemble/build.properties
	server.vendor		= com.li3huo.guide.mvn
	server.version		= 1.0
	scm.tag		= master
	scm.version	= c63e4
	# jenkins BUILD_NUMBER
	jenkins.number		= 22

	➜  mvn_build_number git:(master) ✗ mvn exec:java
	...
	[INFO] ------------------------------------------------------------------------
	[INFO] Building mvn_build_number 1.0
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ mvn_build_number ---
	Server Vendor:	com.li3huo.guide.mvn
	Server Version:	1.0
	SCM Tag:	master
	SCM Version:	c63e4
	Build Number:	22

	➜  mvn_build_number git:(master) ✗ java -cp target/mvn_build_number-1.0.jar com.li3huo.guide.mvn.BuildInfo
	Server Vendor:	com.li3huo.guide.mvn
	Server Version:	1.0
	SCM Tag:	master
	SCM Version:	c63e4
	Build Number:	22
	