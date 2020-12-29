#!/usr/bin/env bash
mvn clean install
mv ./target/generated-sources/annotations/com/serviceMatrix/autofactory/* ././../generatedservice/src/main/java/com/serviceMatrix/autofactory/
cd ././../generatedservice/
mvn clean package