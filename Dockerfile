FROM openjdk:17-jdk-alpine3.13
LABEL maintainer="github.com/thorasine"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]