Source code for the article https://howtodoinjava.com/?p=44114

# Spring Boot Thymeleaf Tutorial

A small playlist app that shows Thymeleaf expressions, iteration, conditionals, links,
forms with Bean Validation, fragments, i18n, formatting and MockMvc / MockMvcTester tests.

## Versions

- Java 25
- Spring Boot 4.1.1
- Thymeleaf 3.1.5.RELEASE (managed by Spring Boot)
- Maven 3.9+

## Run

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Open http://localhost:8080/songs, http://localhost:8080/songs/new and
http://localhost:8080/songs?genre=JAZZ. Send `Accept-Language: de` to see the German messages.

## Test

```bash
mvn test
```

The tests in `TemplateErrorsTest` use the test-only templates in `src/test/resources/templates/broken`
to reproduce `TemplateInputException`, `SpelEvaluationException` and the `th:object` selection error.
