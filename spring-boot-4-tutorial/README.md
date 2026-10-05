Source code for the article https://howtodoinjava.com/?p=44181

# Spring Boot 4 Tutorial (Pantry API)

A small pantry API that shows the main Spring Boot 4.0 and 4.1 features in one app:

- the new modular starters (`spring-boot-starter-webmvc`, `spring-boot-starter-restclient`, `spring-boot-starter-opentelemetry`, `spring-boot-starter-webmvc-test`)
- JSpecify null-safety (`@NullMarked`, `@Nullable`) checked at build time with NullAway
- API versioning with `@GetMapping(version = ...)` and the `API-Version` header
- an HTTP service client (`@HttpExchange` + `@ImportHttpServices`) with `@Retryable`
- Jackson 3 (`tools.jackson`, `JsonMapper`, Spring Boot 4.1 factory constraints)
- `RestTestClient` and `MockMvcTester` tests
- virtual threads
- OpenTelemetry traces over OTLP
- Spring Boot 4.1 `InetAddressFilter` (SSRF protection)

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Jackson 3.1.5, Tomcat 11.0.24, JUnit 6.0.3)
- Java 25
- Maven 3.9+
- Error Prone 2.50.0 and NullAway 0.14.2 (only in the `nullaway` profile)

## Run the tests

```bash
mvn test
```

14 tests: `PantryControllerTest` (MockMvcTester), `PantryApiIntegrationTest` (RestTestClient), `PriceClientTest` (HTTP service client and retry against a stub server), `JacksonThreeTest`, `SsrfFilterTest`.

## Run the app

Optional, to see the traces: start Jaeger, which accepts OTLP on port 4318.

```bash
docker run -d --name jaeger -p 16686:16686 -p 4318:4318 jaegertracing/jaeger:2.21.0
```

Start the app.

```bash
mvn spring-boot:run
```

Call it.

```bash
curl localhost:8080/pantry/apple                      # {"name":"apple","quantity":5}
curl -H "API-Version: 2" localhost:8080/pantry/apple  # {"name":"apple","quantity":5,"bestBefore":"2026-10-12"}
curl localhost:8080/pantry/apple/price                # {"item":"apple","cents":120}
curl localhost:8080/pantry/thread                     # VirtualThread[#59,tomcat-handler-8]/...
```

Open http://localhost:16686 to see the traces of service `pantry`.

## Null checks with NullAway

```bash
mvn -Pnullaway compile                  # passes
mvn -Pnullaway,nullaway-demo compile    # fails: [NullAway] dereferenced expression 'item' is @Nullable
```

The `nullaway-demo` profile adds `src/nullaway-demo/java/.../PantryReport.java`, which calls `item.quantity()` without a null check. `.mvn/jvm.config` holds the `--add-exports` flags that Error Prone needs on JDK 17+.
