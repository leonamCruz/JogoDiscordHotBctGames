FROM maven:4.0.0-rc-4-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

USER nobody

ENTRYPOINT ["java", "-XX:MaxRAM0Percentage=90.0", "-jar", "app.jar"]