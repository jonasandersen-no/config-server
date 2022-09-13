FROM eclipse-temurin:17
MAINTAINER bjoggis.com
ARG JAR_FILE=maven/*.jar
COPY ${JAR_FILE} config-server.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/config-server.jar"]