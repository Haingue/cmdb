## Build core
FROM docker.io/maven:3.9.10-amazoncorretto-21-alpine AS core-builder
WORKDIR /workspace/app
COPY core/pom.xml .
RUN mvn dependency:go-offline -B
COPY core/src ./src
RUN mvn -DskipTests package

FROM docker.io/maven:3.9.10-amazoncorretto-21-alpine AS builder
WORKDIR /workspace/app
COPY InventoryService/pom.xml .
COPY --from=core-builder /workspace/app/target/core-0.0.1-SNAPSHOT.jar /root/.m2/repository/com/management/cmdb/core/0.0.1-SNAPSHOT/core-0.0.1-SNAPSHOT.jar
RUN mvn dependency:go-offline -B
COPY InventoryService/src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/app/target/inventory-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
