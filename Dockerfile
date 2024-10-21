# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Add the wait-for-it.sh script to the Docker image
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh

# Make the wait-for-it.sh script executable
RUN chmod +x /wait-for-it.sh
RUN apt-get update && apt-get install -y telnet

# Copy the project's build file and dependencies
COPY build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Define the command to run the app, waiting for Redis and PostgreSQL to be ready
ENTRYPOINT ["/wait-for-it.sh", "redis:6379", "--", "/wait-for-it.sh", "database:5432", "--", "java", "-jar", "app.jar"]
