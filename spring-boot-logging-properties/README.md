# Spring Boot Logging with application.properties

Source code for the article [Spring Boot Logging with application.properties](https://howtodoinjava.com/spring-boot/logging-application-properties/).

## Versions

- Java 25
- Spring Boot 4.1.1 (Logback, SLF4J, Spring Boot Actuator)

## What the example shows

- `application.properties`: `logging.level.*`, `logging.file.name`, `logging.logback.rollingpolicy.*`,
  `logging.group.*` and the Actuator `loggers` endpoint
- `application-prod.properties`: profile-specific levels and `logging.structured.format.console=ecs`
- `logback-errors-spring.xml`: an extra error-only file for cases that properties cannot cover
  (start with `--logging.config=classpath:logback-errors-spring.xml`, or rename it to `logback-spring.xml`)
- `src/test/resources/yaml/application.yml`: the YAML form of the same settings
- Tests (`OutputCaptureExtension`): default levels, package levels, log groups, console pattern, YAML,
  structured ECS and Logstash JSON, JSON field customization, profiles, console threshold, disabled console,
  ANSI colors, file rotation, `logback-spring.xml`, and changing levels at runtime with `/actuator/loggers`

## Run

```bash
mvn test                # 16 tests
mvn spring-boot:run     # playlist API on port 8080, log file in logs/playlist.log

curl -X POST "localhost:8080/playlists/road-trip/songs?title=Yesterday"
curl -X POST "localhost:8080/playlists/gym/play"
curl localhost:8080/actuator/loggers/com.howtodoinjava.logging.playlist
```
