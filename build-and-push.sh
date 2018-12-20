#!/bin/sh

#mvn clean package
docker build -t ebinterface/validator-web .
docker tag validator-web:latest ebinterface/validator-web:latest
docker push ebinterface/validator-web:latest
