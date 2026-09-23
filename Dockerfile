# Stage 1: Build stage
FROM amazoncorretto:25-alpine AS builder
WORKDIR /app

# Copy gradle wrapper and config files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Fix Windows CRLF line endings if cloned on Windows and ensure execute permissions
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copy source code
COPY src src

# Build executable jar
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Runtime stage
FROM amazoncorretto:25-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
