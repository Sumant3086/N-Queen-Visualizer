# Use Maven image to build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use a smaller Java image to run the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
