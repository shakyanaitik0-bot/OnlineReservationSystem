FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/reservation-system.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]