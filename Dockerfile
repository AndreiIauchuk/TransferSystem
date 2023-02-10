# Build Stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
#TODO volume
RUN mvn package

# Run Stage
FROM openjdk:18-alpine
MAINTAINER Andrei Iauchuk <iovchukandrew@gmail.com>
COPY --from=build /app/target/transfer_system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT java -jar app.jar "import/example-transfer-system.json"