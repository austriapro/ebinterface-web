#!/bin/sh

mvn clean package -DskipTests=true
docker build --pull -t ebinterface/validator-web .
#docker tag validator-web:latest ebinterface/validator-web:latest
docker login -u ebinterface
docker push ebinterface/validator-web:latest
