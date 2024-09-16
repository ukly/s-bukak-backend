FROM openjdk:17-jdk-alpine3.13
ARG JAR_FILE=./build/libs/s-bukak-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]