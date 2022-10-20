FROM tomcat:9-jdk11

ENV JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/urandom" 

RUN rm -rf /usr/local/tomcat/webapps/*
ADD ebinterface-web/target/ebinterface-web.war /usr/local/tomcat/webapps/ROOT.war
