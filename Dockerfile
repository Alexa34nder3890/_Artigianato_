# Fase 1: Compilazione
FROM maven:3.9-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Esecuzione
FROM eclipse-temurin:25-jre-alpine
COPY --from=build target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "/app.jar"]