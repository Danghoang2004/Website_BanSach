FROM eclipse-temurin:17-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy jar file into container
COPY target/web_bansach-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
