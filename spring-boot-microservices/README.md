Source code for the article https://howtodoinjava.com/?p=44113

A small library system built as Spring Boot microservices:

- `book-service` (port 8081): the book catalog, `GET /books/{id}`
- `loan-service` (port 8082): `POST /loans`, calls book-service through a Spring HTTP interface client with Resilience4j retry and circuit breaker
- `gateway` (port 8080): Spring Cloud Gateway Server Web MVC, routes `/api/books/**` and `/api/loans/**`
- `jaeger`: receives the traces of all three services over OTLP (UI on port 16686)

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Framework 7.0.9)
- Spring Cloud 2025.1.3 (Spring Cloud Gateway 5.0.3)
- Resilience4j 2.4.0 (`resilience4j-spring-boot4`)
- Micrometer Tracing 1.7.1 with OpenTelemetry (`spring-boot-starter-opentelemetry`)
- WireMock 3.13.2 (tests)
- Jaeger 2.21.0 (Docker image)
- Maven 3.9+, Docker with Compose v2

## Run the tests

```bash
mvn test
```

## Run everything with Docker Compose

```bash
mvn package -DskipTests
docker compose up -d --build
```

Try it:

```bash
curl -i localhost:8080/api/books/1
curl -i -X POST localhost:8080/api/loans -H 'Content-Type: application/json' -d '{"bookId":"1","member":"Lokesh"}'
docker compose logs | grep "Created loan"
```

Open the Jaeger UI at http://localhost:16686 and search for the service `gateway` to see the traces.

Failure scenarios:

```bash
docker compose stop book-service    # retry, then circuit breaker opens, 503 responses
docker compose start book-service   # after 10 s the circuit closes again
docker compose pause book-service   # read timeout of 2 s per attempt
docker compose unpause book-service
```

Stop everything:

```bash
docker compose down
```

## Run without Docker

Start each module in its own terminal (`mvn spring-boot:run` in `book-service`, `loan-service` and `gateway`). The default URLs in `application.yaml` point to `localhost`. To collect traces in this mode, run a Jaeger container that publishes port 4318 (`docker run -d -p 4318:4318 -p 16686:16686 jaegertracing/jaeger:2.21.0`).
