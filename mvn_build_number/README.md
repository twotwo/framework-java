Demo for buildnumber-maven-plugin
==================================
转载请注明：**Powered by li3huo.com**  
技术交流请关注新浪微博 @li3huo

# 参考资料

## 

### [插件官网](http://www.mojohaus.org/buildnumber-maven-plugin/ "buildnumber-maven-plugin")

### [使用介绍](http://www.mojohaus.org/buildnumber-maven-plugin/usage.html "Usage")

### [stackoverflow](http://stackoverflow.com/questions/14976824/how-can-i-get-jenkins-build-number-svn-revision-number-and-display-it-on-my "帮助")

### [wiki](http://wiki.li3huo.com/Maven "Maven")

# 配置说明

## 创建工程

	➜  framework-java git:(master) ✗  mvn -B archetype:generate \
	  -DarchetypeGroupId=com.li3huo.archetypes \
	  -DarchetypeArtifactId=archetype-simple-project \
	  -DgroupId=com.li3huo.guide.mvn \
	  -DartifactId=mvn_build_number

	➜  framework-java git:(master) ✗ cd mvn_build_number

## 配置pom.xml

创建`<build>`标签，并在`<resources>`添加`<filtering>`；在`<plugins>`中加入`buildnumber-maven-plugin`

[主要参考](https://github.com/mojohaus/mojo-parent/blob/master/pom.xml "scm的github配置")

## 配置build.properties

	➜  mvn_build_number git:(master) ✗ mkdir src/main/resources/assemble
	➜  mvn_build_number git:(master) ✗ cat << EOF >> src/main/resources/assemble/build.properties
	server.vendor		= ${pom.groupId}
	server.version		= ${pom.version}
	server.tag			= ${scmBranch}
	server.buildNumber	= ${buildNumber}
	build_id			= ${env.BUILD_ID}
	EOF

## 编写BuildInfo.java

	➜  mvn_build_number git:(master) ✗ st src/main/java/com/li3huo/guide/mvn/BuildInfo.java

# 使用说明

	➜  mvn_build_number git:(master) ✗ mvn clean package -Dmaven.buildNumber.doCheck=false

	➜  mvn_build_number git:(master) ✗ cat target/classes/assemble/build.properties
		server.vendor		= com.li3huo.guide.mvn
		server.version		= 1.0-SNAPSHOT
		server.tag		= master
		server.buildNumber	= 18f6c
		build_id		= ${env.BUILD_ID}

	➜  mvn_build_number git:(master) ✗ mvn exec:java -Dmaven.buildNumber.doCheck=false
	...
	[INFO] ------------------------------------------------------------------------
	[INFO] Building mvn_build_number 1.0
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ mvn_build_number ---
	server.version: 1.0
	Server Vendor: com.li3huo.guide.mvn
	SCM Branch:   master
	Build Number:  e4bf3
