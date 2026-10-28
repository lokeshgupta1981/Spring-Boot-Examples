# Spring Boot banner demo

This sample uses Spring Boot 4.1.2. Run `mvn package`, then:

```bash
java -jar target/spring-boot-banner-demo-1.0.0.jar
```

The banner is off, but startup log messages remain. To override the property and Java default for one launch:

```bash
java -jar target/spring-boot-banner-demo-1.0.0.jar --spring.main.banner-mode=console
```

This prints the Spring Boot banner before the startup logs.
