# Spring Boot Hibernate 7 Example with Spring Boot 4.1

Source code for the article [Spring Boot Hibernate 7 Example with Spring Boot 4.1](https://howtodoinjava.com/?p=26651).

A bicycle repair shop: mechanics and their repair jobs, stored with Spring Data JPA and Hibernate, with a small REST API.

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Framework 7.0.9, Spring Data 2026.0.1)
- Hibernate ORM 7.4.5.Final (managed by Spring Boot, Jakarta Persistence 3.2)
- H2 2.4.240 (development database)
- MySQL 9 in Docker through Testcontainers 2.0.5 (one test class)
- JUnit 6.0.3
- Maven 3.9 or newer

## Run

```bash
mvn spring-boot:run                                    # http://localhost:8080/api/jobs?status=DONE
mvn test                                               # 23 tests; MySqlRepairJobTest needs Docker
mvn test -Dtest='!MySqlRepairJobTest'                  # without Docker
```

The console prints every SQL statement and its bind parameters (`logging.level.org.hibernate.SQL=debug`,
`logging.level.org.hibernate.orm.jdbc.bind=trace`).

## Files

| File | What it shows |
|---|---|
| pom.xml | `spring-boot-starter-data-jpa`, H2, MySQL driver and the test starters |
| application.properties | Datasource, `ddl-auto`, `open-in-view`, SQL logging |
| data.sql | Two mechanics and four repair jobs for H2 |
| Mechanic.java | Entity with a one-to-many association |
| RepairJob.java | Entity with an enum, a check constraint (Jakarta Persistence 3.2) and `@CreationTimestamp` |
| RepairJobRepository.java | Derived query methods, `@Query`, DTO projection, `@Modifying` update |
| MechanicRepository.java | Derived query by name |
| RepairShopService.java | `@Transactional` service: dirty checking, rollback, read-only transactions |
| RepairJobDao.java | `EntityManager` queries, unwrapping `Session` and `SessionFactory` |
| RepairJobController.java | REST endpoints under `/api/jobs` |
| RepairJobView.java, NewRepairJob.java, MechanicRevenue.java | Records for JSON and projections |
| RepairJobRepositoryTest.java | `@DataJpaTest` tests for the repositories, naming strategy and check constraint |
| RepairShopServiceTest.java | Transactions, rollback, read-only, `LazyInitializationException` |
| HibernateApiTest.java | `EntityManager`, `Session`, `SessionFactory`, removed Hibernate 7 methods |
| SqlLoggingTest.java | SQL and bind parameter logging |
| RepairJobControllerTest.java | MockMvc tests for the REST endpoints |
| MySqlRepairJobTest.java | Same code on MySQL 9 with Testcontainers and `@ServiceConnection` |
