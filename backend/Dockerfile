# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .
RUN ./mvnw dependency:go-offline -q

# Copy backend source
COPY backend/src src/

# Copy frontend static files into Spring Boot resources
COPY index.html src/main/resources/static/index.html
COPY css src/main/resources/static/css
COPY src/app.js src/main/resources/static/src/app.js
COPY src/api.js src/main/resources/static/src/api.js

RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/todoapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
