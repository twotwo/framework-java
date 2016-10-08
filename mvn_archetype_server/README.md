# Creating Archetypes for Server side Project

## Reference
* [Guide to Creating Archetypes](https://maven.apache.org/guides/mini/guide-creating-archetypes.html)

## Overview
An archetype is made up of:

1. a pom for the archetype (pom.xml in the archetype's root directory).
1. an archetype descriptor (archetype.xml in directory: src/main/resources/META-INF/maven/)
1. prototype files that are copied by the archetype plugin (directory: src/main/resources/archetype-resources/)
1. prototype pom (pom.xml in: src/main/resources/archetype-resources)

## Create a new project and pom.xml for the archetype artifact
Create Folder and file

	➜  framework-java git:(master) ✗ mkdir mvn_archetype_server
	➜  framework-java git:(master) ✗ cd mvn_archetype_server 
	➜  mvn_archetype_server git:(master) ✗ st pom.xml

`pom.xml`

	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<modelVersion>4.0.0</modelVersion>
		<groupId>com.li3huo</groupId>
		<artifactId>mvn-archetype-server</artifactId>
		<version>0.1</version>
		<packaging>jar</packaging>
	</project>

## Create the archetype descriptor
Create Folder and file

	➜  mvn_archetype_server git:(master) ✗ mkdir -p src/main/resources/META-INF/maven/
	➜  mvn_archetype_server git:(master) ✗ st src/main/resources/META-INF/maven/archetype.xml

`archetype.xml`

	<archetype xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/plugins/maven-archetype-plugin/archetype/1.0.0 http://maven.apache.org/xsd/archetype-1.0.0.xsd">
		<id>mvn-archetype-server</id>
		<sources>
			<source>src/main/java/com/li3huo/App.java</source>
		</sources>
		<testSources>
			<source>src/test/java/com/li3huo/AppTest.java</source>
		</testSources>
	</archetype>


## Create the prototype files and the prototype pom.xml
Create Folder and file

	➜  mvn_archetype_server git:(master) ✗  mkdir -p src/main/resources/archetype-resources/
	➜  mvn_archetype_server git:(master) ✗  st src/main/resources/archetype-resources/pom.xml

`pom.xml`

	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<modelVersion>4.0.0</modelVersion>

		<groupId>${groupId}</groupId>
		<artifactId>${artifactId}</artifactId>
		<version>${version}</version>
		<packaging>jar</packaging>

		<name>A custom project</name>
		<url>http://li3huo.com</url>

		<properties>
			<jdk.version>1.7</jdk.version>
			<junit.version>4.12</junit.version>
			<log4j.version>2.7</log4j.version>
		</properties>

		<dependencies>
		<dependency>
	  		<groupId>junit</groupId>
	  		<artifactId>junit</artifactId>
			<version>${junit.version}</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-api</artifactId>
			<version>${log4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>${log4j.version}</version>
		</dependency>
		</dependencies>
	</project>

`App.java & AppTest.java`

	➜  mvn_archetype_server git:(master) ✗  cd src/main/resources/archetype-resources/
	➜  archetype-resources git:(master) ✗ mkdir -p src/main/java/com/li3huo src/test/java/com/li3huo

## Install the archetype and run the archetype plugin
Final Structure

	➜  mvn_archetype_server git:(master) ✗ tree .
	.
	├── README.md
	├── pom.xml
	└── src
	    └── main
	        └── resources
	            ├── META-INF
	            │   └── maven
	            │       └── archetype.xml
	            └── archetype-resources
	                ├── pom.xml
	                └── src
	                    ├── main
	                    │   └── java
	                    │       └── com
	                    │           └── li3huo
	                    │               └── App.java
	                    └── test
	                        └── java
	                            └── com
	                                └── li3huo

	15 directories, 5 files

Install the archetype

	➜  mvn_archetype_server git:(master) ✗ mvn install
	...
	[INFO] Installing /opt/local/ide/git_storage/github/framework-java/mvn_archetype_server/pom.xml to /opt/local/tools/java/maven/maven-repo/com/li3huo/mvn-archetype-server/0.1/mvn-archetype-server-0.1.pom
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 1.778 s
	[INFO] Finished at: 2016-10-08T16:45:28+08:00
	[INFO] Final Memory: 13M/246M
	[INFO] ------------------------------------------------------------------------

Generate a project

    mvn -B archetype:generate                      \
      -DarchetypeGroupId=com.li3huo                \
      -DarchetypeArtifactId=mvn-archetype-server   \
      -DarchetypeVersion=0.1           	           \
      -DgroupId=com.li3huo.maven                   \
      -DartifactId=mvn_test

