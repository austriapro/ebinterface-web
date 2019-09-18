FROM tomcat:8.5-jre8
RUN rm -rf /usr/local/tomcat/webapps/*
ADD ebinterface-web/target/ebinterface-web.war /usr/local/tomcat/webapps/ROOT.war
