FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
COPY firebase-service-account.json .
ENV FIREBASE_CONFIG_PATH=/app/firebase-service-account.json
ENTRYPOINT ["java", "-jar", "app.jar"]