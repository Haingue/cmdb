FROM docker.io/maven:3.9.10-amazoncorretto-21-alpine AS builder
WORKDIR /workspace/app
COPY pom.xml .
COPY target/core-0.0.1-SNAPSHOT.jar /root/.m2/repository/com/management/cmdb/core/core-0.0.1-SNAPSHOT.jar
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -DskipTests package

# FROM ghcr.io/graalvm/native-image-community:25-ol8 as builder
# WORKDIR /workspace/app
# COPY pom.xml .
# COPY src ./src
# COPY mvnw ./mvnw
# RUN chmod +x ./mvnw
# RUN ./mvnw -Pnative -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/app/target/inventory-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
