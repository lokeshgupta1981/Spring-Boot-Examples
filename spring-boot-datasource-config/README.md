# Spring Boot DataSource Configuration

Source code for the article [Spring Boot DataSource Configuration](https://howtodoinjava.com/spring-boot2/datasource-configuration/).

## Versions

- Java 25
- Spring Boot 4.1.1 (HikariCP 7.0.2, H2 2.4.240, Testcontainers 2.0.5)

## What the example shows

- `application.properties`: `spring.datasource.*`, `spring.datasource.hikari.*` and `spring.sql.init.mode`
- `AuditDataSourceConfig`: a second DataSource with `@Bean(defaultCandidate = false)` and `DataSourceProperties`, plus its own script initializer
- Tests: auto-configured pool values, `DataSourceBuilder`, embedded database defaults, the missing-url startup failure,
  two DataSource beans with `@Primary`, `@JdbcTest` on H2 and on PostgreSQL with Testcontainers (`@ServiceConnection`)

## Run

```bash
mvn test              # 16 tests; RecipePostgresTest is skipped when Docker is not running
mvn spring-boot:run   # prints both pools and the recipes
```
