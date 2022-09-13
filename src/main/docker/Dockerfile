FROM eclipse-temurin:17
MAINTAINER bjoggis.com
ARG JAR_FILE=maven/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app.jar"]