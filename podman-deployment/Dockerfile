#pulling jre image
FROM docker.io/library/openjdk:17-oracle
#setting db environment varilable
ENV SPRING_DATASOURCE_URL jdbc:postgresql://10.88.0.14:5432/pgadmin
ENV SPRING_DATASOURCE_USERNAME pgadmin
ENV SPRING_DATASOURCE_PASSWORD postgres@dm1n
#set working dir
WORKDIR /app
COPY demo-0.0.1-SNAPSHOT.jar app.jar
# Expose the port that your Spring Boot application listens on (default is 8080)
EXPOSE 8080
# Define the command to run your application
CMD ["java","-Dserver.port=8080","-jar","app.jar","spring.datasource.url=${SPRING_DATASOURCE_URL}", "spring.datasource.username=${SPRING_DATASOURCE_USERNAME}","spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}"]

