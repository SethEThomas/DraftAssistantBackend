#
# Build stage - dependencies
#
FROM maven:3.8.3-openjdk-17 AS dependencies
WORKDIR /home/app
COPY pom.xml .
RUN mvn dependency:go-offline

#
# Build stage - application
#
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /home/app

# Copy dependencies from the dependencies stage
COPY --from=dependencies /root/.m2 /root/.m2

# Copy the source code and pom.xml
COPY src ./src
COPY pom.xml .

# Build the application
RUN mvn clean package

# Debugging step: List contents of the target directory
RUN ls -l /home/app/target/

#
# Package stage
#
FROM openjdk:17-jdk-slim
# Install MySQL client
RUN apt-get update && \
    apt-get install -y default-mysql-client && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the built jar from the build stage
COPY --from=build /home/app/target/*.jar /usr/local/lib/spring_rest_docker.jar
COPY wait-for-mysql.sh /usr/local/bin/wait-for-mysql.sh
RUN chmod +x /usr/local/bin/wait-for-mysql.sh

# Expose the application port
EXPOSE 8080

# Define the entry point
ENTRYPOINT ["wait-for-mysql.sh", "mysqldb", "java", "-jar", "/usr/local/lib/spring_rest_docker.jar"]
