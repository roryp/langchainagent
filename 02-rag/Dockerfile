# Multi-stage build for Java Spring Boot RAG application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy parent pom first
COPY pom.xml ./pom.xml

# Copy module pom and source
COPY 02-rag/pom.xml ./02-rag/pom.xml

# Download dependencies
WORKDIR /workspace/02-rag
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY 02-rag/src ./src

# Build the application with Spring Boot repackage
RUN mvn clean package spring-boot:repackage -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/02-rag/target/rag-0.1.0.jar app.jar

EXPOSE 8081

# Run with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "/app/app.jar"]
