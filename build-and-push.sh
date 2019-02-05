#!/bin/sh

mvn clean package -DskipTests=true
docker build -t ebinterface/validator-web .
docker tag validator-web:latest ebinterface/validator-web:latest
docker push ebinterface/validator-web:latest
