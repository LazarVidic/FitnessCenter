# ===== BUILD =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# skinemo deps (može da failuje ako ti je net slab; ako pravi problem, skloni ovaj korak)
RUN mvn -B -q -DskipTests dependency:go-offline || true

COPY src src
RUN mvn -B -DskipTests package

# ===== RUN =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
