FROM maven:3.9.8-eclipse-temurin-23
FROM  eclipse-temurin:23-jdk
WORKDIR /app
COPY target/AhmedMohmoud-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]