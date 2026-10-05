Source code for the article https://howtodoinjava.com/spring-data/spring-boot-redis-with-lettuce-jedis/

# Spring Data Redis with Lettuce and Jedis

A small recipe cache that shows how Spring Boot connects to Redis:

- the auto-configured `StringRedisTemplate` and a typed `RedisTemplate<String, Recipe>` with a JSON value serializer
- a `@RedisHash` entity and a `CrudRepository`
- the Lettuce client (default) and the Jedis client, switched with `spring.data.redis.client-type`
- a connection pool from `commons-pool2`, set with `spring.data.redis.lettuce.pool.*` and `spring.data.redis.jedis.pool.*`
- `LettuceClientConfigurationBuilderCustomizer` for a command timeout and a client name, and an own `LettuceConnectionFactory` bean with `LettucePoolingClientConfiguration` (profile `custom-factory`)
- tests against a Redis container started by Testcontainers with `@ServiceConnection`

## Versions

- Spring Boot 4.1.1 (Spring Data Redis 4.1.1, Lettuce 7.5.2, Jedis 7.4.1, commons-pool2 2.13.1, Testcontainers 2.0.5)
- Java 25
- Maven 3.9+
- Docker (for the tests and for the local Redis server)

## Run the tests

Docker must be running. Testcontainers pulls `redis:8.2` on the first run.

```bash
mvn test
```

7 tests: `LettuceRedisTemplateTest` (connection factory, pool, string values, JSON values, hashes), `RecipeRepositoryTest` (`@RedisHash` and TTL), `JedisClientTest` (the same app with `spring.data.redis.client-type=jedis`).

## Run the app

Start a Redis server on localhost:6379.

```bash
docker run -d --name redis-demo -p 6379:6379 redis:latest
```

Run the app with Lettuce (default) or with Jedis. The `RedisDemoRunner` writes a few keys and prints them.

```bash
mvn spring-boot:run
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.data.redis.client-type=jedis
mvn spring-boot:run -Dspring-boot.run.profiles=custom-factory     # own LettuceConnectionFactory bean
```

Output:

```
Connection factory : LettuceConnectionFactory
GET recipe:1:name  : Pancakes
TTL recipe:1:name  : 600
INCR recipe:1:views: 1
INCR recipe:1:views: 2
GET recipe:2       : Recipe[id=2, name=Omelette, minutes=10]
repository.save    : Recipe[id=3, name=Lemonade, minutes=5]
repository.findById: Recipe[id=3, name=Lemonade, minutes=5]
repository.count   : 1
```

Check the keys with the Redis CLI.

```bash
docker exec redis-demo redis-cli KEYS '*'
docker exec redis-demo redis-cli GET recipe:2        # {"id":"2","name":"Omelette","minutes":10,"ttlSeconds":null}
docker exec redis-demo redis-cli HGETALL recipe:3
```

Stop and remove the server when done.

```bash
docker rm -f redis-demo
```
