# Urban Pulse Java API — multi-stage build (Java 21).
# Default runtime uses H2 in-memory unless SPRING_DATASOURCE_* is overridden.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -B -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/api-java-0.0.1-SNAPSHOT.jar /app/app.jar
ENV SERVER_PORT=3847 \
    URBAN_PULSE_KAFKA_ENABLED=false \
    KAFKA_BOOTSTRAP=redpanda:9092 \
    URBANPULSE_ML_BASE_URL=http://ml:3851
EXPOSE 3847
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
