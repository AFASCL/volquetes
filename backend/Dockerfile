# Build stage — Maven (imagen oficial para build fiable en Render)
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests

COPY src ./src
RUN mvn -B package -DskipTests && mv target/volquetes-backend-*.jar target/app.jar

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN adduser -D -u 1000 appuser
COPY --from=builder /app/target/app.jar ./app.jar
USER appuser

# Render inyecta PORT; Spring Boot lo lee desde application.yml (PORT)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
