# Uber-JAR Sample

Uber-JAR就是把整个工程都放到一个jar中的行业叫法。实现方式很多，推荐使用Maven-Shade-Plugin方式。

## 1. Reference
* [Uber-JAR](http://imagej.net/Uber-JAR)
* [Apache Maven Shade Plugin](http://maven.apache.org/plugins/maven-shade-plugin/)
* [executable-jar-with-maven-example](https://github.com/jinahya/executable-jar-with-maven-example)
* [Maven – Create a fat Jar file – One-JAR example](http://www.mkyong.com/maven/maven-create-a-fat-jar-file-one-jar-example/)

### 1.1 maven-shade-plugin

	mvn shade:help -Ddetail=true

## 2. Create Uber-Jar Project

Tools used :

1. Maven 3.2.5
1. JDK 1.7.0

### Create a simple Java project

	➜  framework-java git:(master) ✗ mvn -B archetype:generate    \
	  -DarchetypeGroupId=com.li3huo                \
	  -DarchetypeArtifactId=mvn-archetype-server   \
	  -DarchetypeVersion=0.1           	           \
	  -DgroupId=com.li3huo.maven                   \
	  -DartifactId=mvn_uber_jar
	➜  framework-java git:(master) ✗ cd mvn_uber_jar
	➜  mvn_uber_jar git:(master) ✗ mvn eclipse:eclipse

### Update Pom.xml

	<build>
		<finalName>webserver</finalName>
		<plugins>

			<!-- Set a compiler level -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.5</version>
				<configuration>
					<source>${jdk.version}</source>
					<target>${jdk.version}</target>
				</configuration>
			</plugin>

			<!-- Maven Shade Plugin -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.0.0</version>
				<executions>
					<!-- Run shade goal on package phase -->
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<transformers>
								<!-- add Main-Class to manifest file -->
								<transformer
									implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>com.li3huo.maven.App</mainClass>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>

		</plugins>
	</build>

### Package It
	➜  mvn_uber_jar git:(master) ✗ mvn clean package

### Review It
	➜  mvn_uber_jar git:(master) ✗ jar tf target/webserver.jar

## 3. Run Uber-Jar Project: Netty TimeServer Sample(com.li3huo.service)
* [User guide for 4.x](http://netty.io/wiki/user-guide-for-4.x.html)

### Print App Version

	➜  mvn_uber_jar git:(master) ✗ java -jar target/webserver.jar -version
	18:16:13.023 App [main] Server Vendor:	com.li3huo.maven
	18:16:13.025 App [main] Server Version:	1.0
	18:16:13.025 App [main] SCM Tag:	master
	18:16:13.025 App [main] SCM Version:	8de01
	18:16:13.025 App [main] Build Number:	22

### Start time server

	➜  mvn_uber_jar git:(master) ✗ sudo java -jar target/webserver.jar -server

### Print Time

	➜  w2016 rdate -p localhost
	rdate: [localhost]	Wed Oct 12 19:20:24 2016

	

