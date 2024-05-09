FROM ubuntu:latest
LABEL authors="thewh"

FROM jelastic/maven:3.9.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/authenticationService.jar /authenticationProcess.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/authenticationProcess.jar"]

