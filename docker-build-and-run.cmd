@echo off

call mvn clean package -DskipTests=true
docker build --pull -t ebinterface/validator-web .
docker run -d --name ebinterface-web -p 8888:8080 ebinterface/validator-web
echo Now you can open http://localhost:8888
