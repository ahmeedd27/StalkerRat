# Stage 1: Build the app
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app
# Install Maven
RUN apt-get update && apt-get install -y maven
# Copy Maven files and source code
COPY pom.xml .
COPY src ./src
# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:23-jdk
WORKDIR /app
# Copy the JAR from the builder stage
COPY --from=builder /app/target/AhmedMohmoud-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]