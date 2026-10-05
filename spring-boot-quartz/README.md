# Spring Boot Quartz Scheduler: Jobs, Triggers, JDBC Store

Source code for the article [Spring Boot Quartz Scheduler: Jobs, Triggers, JDBC Store](https://howtodoinjava.com/?p=39677).

A small library application that emails due-date reminders every morning and calculates overdue fines every night with Quartz. No real email is sent: every reminder and fine notice is logged and stored in the `sent_message` table.

## Versions

- Java 25
- Spring Boot 4.1.1 (spring-boot-starter-quartz, -jdbc, -webmvc, -actuator)
- Quartz 2.5.2 (managed by Spring Boot 4.1.1)
- H2 2.4.240
- JUnit 6.1.3, Awaitility 4.3.0, Testcontainers 2.0.5 with PostgreSQL 18.6 (three tests need Docker)
- Maven 3.9 or newer

## Run

```bash
mvn spring-boot:run                                       # RAMJobStore (in memory)
mvn spring-boot:run -Dspring-boot.run.profiles=jdbc       # JDBC JobStore in the H2 file ./data/library
mvn test                                                  # 36 tests (Docker needed for 3 of them)
```

Try it with curl while the application runs:

```bash
curl localhost:8080/actuator/quartz
curl localhost:8080/actuator/quartz/jobs/library/overdueFineJob
curl -X POST localhost:8080/actuator/quartz/jobs/library/overdueFineJob \
     -H "Content-Type: application/json" -d '{"state":"running"}'   # runs the fine job, then the notice job
curl -X POST "localhost:8080/loans/2/reminder?inSeconds=10"           # one-time reminder in 10 seconds
```

## Files

| File | What it shows |
|---|---|
| pom.xml | Spring Boot 4.1.1 parent and the Quartz starter |
| application.properties | RAMJobStore, thread pool size, actuator exposure |
| application-jdbc.properties | JDBC JobStore with schema initialization |
| schema.sql, data.sql | Library tables and five sample loans |
| QuartzConfig.java | JobDetail and Trigger beans, listeners, JobChainingJobListener |
| jobs/DueDateReminderJob.java | Job with a Spring bean (constructor) and a JobDataMap value (setter), `@DisallowConcurrentExecution` |
| jobs/OverdueFineJob.java | `@PersistJobDataAfterExecution` run counter |
| jobs/FineNoticeJob.java | Durable job without a trigger, started by the chain |
| jobs/LoanReminderScheduler.java | Schedules a one-time reminder with the `Scheduler` API and a `SimpleTrigger` |
| jobs/LoanReminderController.java | `POST /loans/{id}/reminder` |
| listeners/JobAuditListener.java | `JobListener` that logs and records each run |
| listeners/TriggerAuditListener.java | `TriggerListener` that logs misfires |
| library/*.java | Loans, fines, reminder and fine services, `JdbcClient` repository |
| LibraryJobsTest.java | Reminder, fine, chaining, job data and one-time reminder |
| ConcurrencyTest.java | `@DisallowConcurrentExecution` and `@PersistJobDataAfterExecution` |
| MisfireTest.java | Eight misfire instructions after a paused scheduler |
| CronExpressionTest.java | Next fire times, Quartz vs Spring day-of-week numbers, invalid expressions |
| ChainingTest.java | `JobChainingJobListener` after a failure, and a listener that chains only on success |
| QuartzActuatorTest.java, ShowValuesActuatorTest.java | The `/actuator/quartz` endpoint |
| JdbcJobStoreTest.java | Jobs and job data survive a restart with H2, a reminder missed while the app was down runs at startup, `initialize-schema=embedded` with a file database, non-serializable job data |
| PostgresRestartTest.java | `PostgreSQLDelegate`, and `initialize-schema=always` deleting jobs on PostgreSQL |
| ClusterTest.java | Two clustered nodes on one PostgreSQL database |
| PlainQuartzTest.java | Quartz without Spring cannot create a job that needs a bean |
