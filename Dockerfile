# Multistage (without Nexus)
# Build Stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package

# Run Stage
FROM openjdk:18-alpine
MAINTAINER Andrei Iauchuk <iovchukandrew@gmail.com>
COPY --from=build /app/target/transfer_system-0.0.1-SNAPSHOT.jar app.jar
CMD java -jar app.jar "import/example-transfer-system.json"


# Single stage (with Nexus)
#FROM maven:3.8.7-openjdk-18
#COPY settings.xml /root/.m2/
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#ENTRYPOINT ["/bin/sh", "-c" , "mvn package \
#            && cp target/transfer_system-0.0.1-SNAPSHOT.jar app.jar \
#            && java -jar app.jar import/example-transfer-system.json"]