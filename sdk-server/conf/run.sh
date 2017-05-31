#!/bin/sh
LANG=en_US.UTF-8
rem exec java -cp ./conf -jar sdk-server.jar -server
java -cp src/main/resources:target/sdk-server.jar com.li3huo.sdk.App -server