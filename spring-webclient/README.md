Source code for the article https://howtodoinjava.com/spring-webflux/webclient-get-post-example/

# Spring WebClient GET and POST Examples

A small Spring Boot app with an in-memory recipes REST API (`RecipeController`) and a `WebClient`
based client (`RecipeClient`) that calls it. The tests start the app on a random port, run the
calls and print the results.

What the client shows:

- GET one object with `retrieve()` and `bodyToMono()`
- GET a JSON array with `bodyToFlux()` and with `ParameterizedTypeReference`
- query parameters with `UriBuilder`, default and per-request headers
- POST with `bodyValue()` and `toEntity()` (status, `Location` header and body)
- DELETE with `toBodilessEntity()`
- error handling with `onStatus()` and `exchangeToMono()`
- timeouts with `spring.http.clients.*` properties and the `timeout()` operator
- `block()` and `subscribe()`
- request logging with `logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions=DEBUG`
- testing the client against the real app and against MockWebServer

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Reactor Netty, JUnit 6.0.3)
- MockWebServer 5.5.0
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

15 tests: `RecipeClientTest` (11, against the running app) and `RecipeClientMockServerTest` (4, against MockWebServer).

## Run the app

```bash
mvn spring-boot:run
curl localhost:8080/recipes                 # [{"id":1,"name":"Pancakes","minutes":20}, ...]
curl localhost:8080/recipes/1               # {"id":1,"name":"Pancakes","minutes":20}
curl -X POST -H "Content-Type: application/json" -d '{"name":"Salad","minutes":5}' localhost:8080/recipes
```
