# Spring Boot Elasticsearch Configuration with Spring Data

Source code for the article [Spring Boot Elasticsearch Configuration with Spring Data](https://howtodoinjava.com/?p=32774).

A used-car marketplace search (`CarListing`: make, model, year, price, mileage, description, city) with
Spring Data Elasticsearch: repository derived queries and `@Query`, `ElasticsearchOperations` with
`CriteriaQuery` and `NativeQuery`, partial updates, delete by query, aggregations, highlighting and pagination.

## Versions

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring Data Elasticsearch | 6.1.1 (managed by Spring Boot) |
| Elasticsearch Java client (co.elastic.clients) | 9.4.5 (managed by Spring Boot) |
| Elasticsearch server | 9.4.5 (tests also pass on 9.5.3) |
| Testcontainers | 2.0.5 |
| JUnit | 6.1.3 |

## Run

The tests start Elasticsearch with Testcontainers, so they need Docker:

```bash
mvn test
# against another server version:
mvn test -Des.image=docker.elastic.co/elasticsearch/elasticsearch:9.5.3
```

To run the application, start a local node with security turned off (local development only):

```bash
docker run -d --name es-cars -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
  docker.elastic.co/elasticsearch/elasticsearch:9.4.5

mvn spring-boot:run
```

`DemoRunner` indexes the eight listings and logs a few searches. The Elasticsearch client keeps the
JVM running; stop the application with Ctrl+C. Set `cars.demo.enabled=false` to skip the demo.

If the index stays red with "No shard available", the disk of the Docker host is above the
Elasticsearch high watermark (90%). For a local node, add `-e "cluster.routing.allocation.disk.threshold_enabled=false"`.

## Files

| File | What it shows |
|---|---|
| `CarListing.java` | `@Document`, `@Setting`, `@Field` mapping |
| `CarListingRepository.java` | Derived queries, `@Query`, `@Highlight`, paging, delete by derived query |
| `CarSearchService.java` | `ElasticsearchOperations`: `CriteriaQuery`, `NativeQuery` bool query, match/fuzzy, `UpdateQuery`, `DeleteQuery`, terms and avg aggregations, highlighting, `SearchPage` |
| `ManualClientConfig.java` | `ElasticsearchConfiguration` subclass (profile `manual-client`): HTTPS with CA fingerprint and basic auth |
| `CarData.java` | The eight sample listings |
| `DemoRunner.java` | Indexes the listings and logs searches at startup |
| `application.properties` | `spring.elasticsearch.*` connection properties |
| `ElasticsearchTestConfig.java` | Testcontainers `ElasticsearchContainer` with `@ServiceConnection` |
| `CarListingRepositoryTest.java` | 9 tests: mapping, derived queries, `@Query`, highlighting, paging, update, delete |
| `CarSearchServiceTest.java` | 14 tests: refresh, criteria, native, full text, update, delete, aggregations, highlighting, pagination, Java client |
| `SecuredClusterTest.java` | Security on: `@Ssl @ServiceConnection` over HTTPS |
| `ManualClientConfigTest.java` | The `ElasticsearchConfiguration` subclass against a secured node |
