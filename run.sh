#!/bin/bash

export JAVA_HOME=/opt/jdk1.8.0/jre
mvn package

head -n 172 project_output/data.txt > project_output/train.txt
tail -n 688 project_output/data.txt > project_output/test.txt

mallet import-file --input project_output/train.txt --output project_output/train.vectors
mallet import-file --input project_output/test.txt --output project_output/test.vectors --use-pipe-from project_output/train.vectors

vectors2classify --training-file project_output/train.vectors  --testing-file project_output/test.vectors --trainer MaxEnt --output-classifier project_output/me-model      > project_output/me.stdout 2>project_output/me.stderr
