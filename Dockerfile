FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/pos-ontheworld-backend-1.0.0.jar ./pos-ontheworld-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pos-ontheworld-backend.jar"]
