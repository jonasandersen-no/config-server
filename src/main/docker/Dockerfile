FROM eclipse-temurin:17-alpine
MAINTAINER bjoggis.com
ARG JAR_FILE=maven/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app.jar"]