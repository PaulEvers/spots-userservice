FROM openjdk:11-jdk-slim

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} spots-userservice.jar

ENTRYPOINT ["java","-jar","/spots-userservice.jar"]