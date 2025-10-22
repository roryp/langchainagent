# Multi-stage build using Microsoft Container Registry images (avoiding Docker Hub)
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS build

WORKDIR /workspace

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy parent pom first
COPY pom.xml ./pom.xml

# Copy module pom and source
COPY 04-tools/pom.xml ./04-tools/pom.xml

# Download dependencies
WORKDIR /workspace/04-tools
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY 04-tools/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/04-tools/target/agents-*.jar app.jar

EXPOSE 8082

# Run with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
