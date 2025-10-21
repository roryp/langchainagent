# Multi-stage build for Java Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy parent pom first
COPY pom.xml ./pom.xml

# Copy module pom and source
COPY 01-introduction/pom.xml ./01-introduction/pom.xml

# Download dependencies
WORKDIR /workspace/01-introduction
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY 01-introduction/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/01-introduction/target/introduction-*.jar app.jar

EXPOSE 8080

# Run with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "/app/app.jar"]
