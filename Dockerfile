FROM openjdk:17-jdk-alpine

ENV mysql_user =  dev
ENV mysql_password = mysql

COPY target/*.jar /api.jar
ENTRYPOINT ["java","-jar","/api.jar"]