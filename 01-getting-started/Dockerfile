FROM eclipse-temurin:21-jre

# Copy the packaged application into the container.  We assume
# 'mvn package' has been run before building the image.
WORKDIR /app
COPY target/getting-started-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]