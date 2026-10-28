# Spring Boot application arguments

Requires Java 17+ and Maven. Uses Spring Boot 4.1.2.

```bash
mvn package
java -jar target/spring-boot-arguments-demo-1.0.0.jar orders.csv --region=west --dry-run --import.batch-size=50
```

The application previews an import configuration. It does not read or import `orders.csv`.

