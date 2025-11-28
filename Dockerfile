# Build Stage
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy everything to container
COPY . .

# Build the Spring Boot fat JAR
RUN ./gradlew clean bootJar --no-daemon

# Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]