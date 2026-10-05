Source code for the article https://howtodoinjava.com/?p=44106

# Spring Cloud Gateway Example

An API gateway for a small recipe service, built with Spring Cloud Gateway.

| Module | Port | What it shows |
|---|---|---|
| recipe-service | 8081 | The downstream REST service (normal, flaky and slow endpoints) |
| gateway-webflux | 8080 | Spring Cloud Gateway Server WebFlux: YAML and Java routes, path rewriting, headers, Redis rate limiting, retry, Resilience4j circuit breaker, CORS, actuator |
| gateway-webmvc | 8090 | Spring Cloud Gateway Server Web MVC: the same recipe route with the Java routes API |

## Versions

- Java 25
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3 (Spring Cloud Gateway 5.0.3, Spring Cloud CircuitBreaker 5.0.3, Resilience4j 2.3.0)
- Redis 8.10.2 (Docker)
- WireMock 3.13.2 and Testcontainers 2.0.5 (tests)

## Run the tests

Docker must be running (Testcontainers starts Redis). The WebFlux tests start WireMock on port 8081,
so stop recipe-service before running them.

```bash
mvn test
```

## Run the apps

```bash
docker run -d --name recipe-redis -p 6379:6379 redis:8.10.2

mvn -DskipTests package
java -jar recipe-service/target/recipe-service-1.0.0.jar
java -jar gateway-webflux/target/gateway-webflux-1.0.0.jar
java -jar gateway-webmvc/target/gateway-webmvc-1.0.0.jar
```

## Try it

```bash
curl -i http://localhost:8080/api/recipes/pancakes -H "X-User: lokesh"   # rewrite + headers
for i in 1 2 3 4; do curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/recipes/pancakes -H "X-User: alex"; done   # 429
curl http://localhost:8080/api/flaky/recipes                               # retry
curl http://localhost:8080/api/slow/recipes                                # circuit breaker fallback
curl -i -X OPTIONS http://localhost:8080/api/recipes/pancakes \
  -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: GET"   # CORS preflight
curl http://localhost:8080/actuator/gateway/routes                         # actuator
curl -i http://localhost:8090/api/recipes/omelette                         # Web MVC gateway
```
