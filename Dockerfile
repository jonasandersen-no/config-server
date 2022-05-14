FROM openjdk:17
MAINTAINER bjoggis.com
COPY target/config-server-1.0.0-SNAPSHOT.jar config-server.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/config-server.jar"]