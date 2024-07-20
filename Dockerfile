#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn clean package

# Debugging step: List contents of the target directory
RUN ls -l /home/app/target/

#
# Package stage
#
FROM openjdk:17-jdk-slim
# Use the correct path to copy the JAR file
COPY --from=build /home/app/target/*.jar /usr/local/lib/spring_rest_docker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/spring_rest_docker.jar"]
