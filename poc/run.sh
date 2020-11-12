#!/bin/bash
JAVA_PATH=/home/kettil/.jdks/loom-jdk-16/bin/java
$JAVA_PATH --enable-preview -jar target/poc-1.0-SNAPSHOT-jar-with-dependencies.jar
