Source code for the article https://howtodoinjava.com/?p=44194

# Spring Framework 7 Features (Library API)

Short, tested examples of the main Spring Framework 7 changes. The `core` package runs without Spring Boot; the `web` package is a small Spring Boot 4.1 library API.

- `BeanRegistrar` for programmatic bean registration (one `HelpDesk` bean per configured branch)
- `@Retryable` and `@ConcurrencyLimit` enabled with `@EnableResilientMethods`
- `Optional` with the SpEL safe navigation (`?.`) and Elvis (`?:`) operators
- JSpecify null-safety (`@NullMarked`, `@Nullable`)
- API versioning with a `version` query parameter (`configureApiVersioning`)
- HTTP service groups with `@ImportHttpServices` and `RestClientHttpServiceGroupConfigurer`
- Jackson 3 `JsonMapper` set through `HttpMessageConverters`
- `RestTestClient` tests

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Jackson 3.1.5, JUnit 6.0.3)
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

12 tests:

- `PlainSpringContextTest` (Spring without Spring Boot): retry, concurrency limit, `BeanRegistrar`
- `SpelOptionalTest`: `Optional` in SpEL
- `BookApiTest`: API versioning, Jackson 3 converter, `RestTestClient`, `@Nullable` lookup
- `StockClientTest`: HTTP service group client against a stub server

## Run the app

```bash
mvn spring-boot:run
```

```bash
curl localhost:8080/books/dune              # {"title":"dune","copies":3}
curl "localhost:8080/books/dune?version=2"  # {"title":"dune","copies":3,"shelf":"A3"}
curl "localhost:8080/books/emma?version=2"  # {"title":"emma","copies":0}
curl "localhost:8080/books/dune?version=3"  # 400 Bad Request (InvalidApiVersionException)
```

The `StockClient` calls the URL in `stock.base-url` (default `http://localhost:8091`).
