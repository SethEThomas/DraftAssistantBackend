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
# Install MySQL client
RUN apt-get update && \
    apt-get install -y default-mysql-client && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /home/app/target/*.jar /usr/local/lib/spring_rest_docker.jar
COPY wait-for-mysql.sh /usr/local/bin/wait-for-mysql.sh
RUN chmod +x /usr/local/bin/wait-for-mysql.sh
EXPOSE 8080
ENTRYPOINT ["wait-for-mysql.sh", "mysqldb", "java", "-jar", "/usr/local/lib/spring_rest_docker.jar"]
