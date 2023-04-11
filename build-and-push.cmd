@echo off
call mvn clean install -DskipTests
if errorlevel 1 goto error

docker build --pull -t ebinterface/validator-web .
if errorlevel 1 goto error

docker login -u ebinterface
if errorlevel 1 goto error

docker push ebinterface/validator-web:latest
if errorlevel 1 goto error

goto end
:error
echo Error Occured
:end
