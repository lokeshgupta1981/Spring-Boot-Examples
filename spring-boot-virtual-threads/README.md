# Spring Boot Virtual Threads: Setup, Load Test and WebFlux

Source code for the article [Spring Boot Virtual Threads: Setup, Load Test and WebFlux](https://howtodoinjava.com/?p=40210).

A weather API (`GET /weather/{city}`) that calls two slow downstream services, run three ways:
Spring MVC on platform threads, Spring MVC on virtual threads, and Spring WebFlux.

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Framework 7.0.9, Tomcat 11.0.24, Reactor Netty 1.3.7)
- JUnit 6.1.3
- Load tests: [hey](https://pkg.go.dev/github.com/rakyll/hey) (`go install github.com/rakyll/hey@latest`)

## Run

```bash
# 1. The slow downstream services on port 9090 (every answer after 250 ms)
mvn spring-boot:run -Dspring-boot.run.main-class=com.howtodoinjava.virtualthreads.stub.StubApplication

# 2. The weather API on virtual threads, port 8080
mvn spring-boot:run
curl localhost:8080/weather/london
curl localhost:8080/weather/london/parallel

# 2b. The same API on platform threads
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.threads.virtual.enabled=false

# 3. The WebFlux version, port 8081
mvn spring-boot:run -Dspring-boot.run.main-class=com.howtodoinjava.virtualthreads.reactive.ReactiveWeatherApplication
curl localhost:8081/weather/london

# Load test (warm-up, then 10,000 requests from 1,000 users)
./loadtest.sh http://localhost:8080/weather/london 1000 10000

# Tests
mvn test
```

For load tests, start the weather API with `--weather.air-quality-limit=10000` so that the
semaphore does not limit the results.

## Files

| File | What it shows |
|---|---|
| `stub/StubApplication.java`, `stub/StubController.java` | The slow forecast and air quality services (WebFlux, `delayElement()`) |
| `mvc/WeatherController.java` | Blocking endpoint and the parallel version with `Executors.newVirtualThreadPerTaskExecutor()` |
| `mvc/WeatherClient.java` | `RestClient` calls and a `Semaphore` that limits air quality calls |
| `mvc/ReportAuditService.java` | An `@Async` method that returns the thread it ran on |
| `reactive/ReactiveWeatherController.java` | The same API with `WebClient`, `flatMap()` and `Mono.zip()` |
| `src/main/resources/application.properties` | `spring.threads.virtual.enabled=true` and the API settings |
| `VirtualThreadsTest.java` | Requests, `@Async`, scheduler and `HttpClient` run on virtual threads; parallel timing; semaphore limit |
| `PlatformThreadsTest.java` | The same checks with `spring.threads.virtual.enabled=false` |
| `ReactiveWeatherTest.java` | The WebFlux version runs on Netty event loop threads |
| `CustomExecutorTest.java` | A custom `Executor` bean switches off the virtual `@Async` executor |
| `PinningTest.java` | JFR `jdk.VirtualThreadPinned` events: none for `synchronized`, events for native callbacks and class initializers |
| `loadtest.sh` | The hey commands used for the load tests |
