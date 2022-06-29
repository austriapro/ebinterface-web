@echo off

call mvn clean package -DskipTests=true
if errorlevel 1 goto error

docker build --pull -t ebinterface/validator-web .
if errorlevel 1 goto error

docker run -d --name ebinterface-web -p 8888:8080 ebinterface/validator-web
if errorlevel 1 goto error

echo Now you can open http://localhost:8888
goto done

:error
echo ERROR

:done
