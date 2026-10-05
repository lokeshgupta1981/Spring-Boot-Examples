# Build the jar first: mvn -DskipTests package
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/greeting-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
