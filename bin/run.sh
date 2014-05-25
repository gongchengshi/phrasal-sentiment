#!/bin/bash

export JAVA_HOME=/opt/jdk1.8.0/jre
mvn package

/opt/jdk1.8.0/jre/bin/java -jar target/phrasal_sentiment-1.0-SNAPSHOT-jar-with-dependencies.jar

csv2vectors --input project_output/data.txt --output data.vectors
vectors2classify --input data.vectors --training-portion 0.6 --num-trials 3 --trainer MaxEnt