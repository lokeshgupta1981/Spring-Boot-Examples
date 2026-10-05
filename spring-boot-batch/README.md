Source code for the article https://howtodoinjava.com/spring-batch/spring-boot-batch-tutorial-example/

# Spring Batch Example with Spring Boot

A Spring Batch 6 job that reads `books.csv`, upper-cases the titles, drops rows without an author, skips rows with a bad page count, and writes the rest into an H2 table.

- `jobs/` is the example for the article above (`BatchProcessingApplication`).
- `quartz/` is the example for https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/ (`BatchJobByQuartzApplication`, profile `quartz`).

## Versions

- Spring Boot 4.1.1 (Spring Batch 6.0.5, Spring Framework 7.0.9)
- Java 25
- H2 2.4.240 (in memory)

## Run the tests

```bash
mvn test
```

4 tests in `BookImportJobTest` (job completes with read=6, filtered=1, skipped=1, written=5; the same parameters cannot run twice; a restart creates a new execution of the same instance; metadata is stored in `BATCH_JOB_INSTANCE`).

## Run the job on startup

```bash
mvn spring-boot:run
# or, with a job parameter
mvn package -DskipTests
java -jar target/spring-boot-batch-1.0-SNAPSHOT.jar inputFile=books.csv
```

## Run the job on demand

Set `spring.batch.job.enabled=false` (or pass `--spring.batch.job.enabled=false`) and call the endpoint.

```bash
curl -X POST localhost:8080/books/import                   # Job 1 finished with status COMPLETED
curl -X POST "localhost:8080/books/import?file=missing.csv" # Job 2 finished with status FAILED
```

The H2 console is at http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:booksdb`, user `sa`, empty password).

## Run the Quartz example

```bash
mvn compile
java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) com.howtodoinjava.demo.batch.quartz.BatchJobByQuartzApplication
```
