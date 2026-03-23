FROM eclipse-temurin:11-jdk

WORKDIR /app

COPY target/reservation-system.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]