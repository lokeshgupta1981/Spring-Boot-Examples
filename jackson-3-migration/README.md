Source code for the article https://howtodoinjava.com/?p=44195

# Jackson 3 Migration: From Jackson 2 to Jackson 3

Jackson 2 and Jackson 3 side by side. Jackson 3 uses the `tools.jackson` packages, so both versions are on the same classpath and every test runs the same input through both of them.

| Module | What it shows |
|---|---|
| `core-changes` | Jackson 2.22 vs Jackson 3.2: `JsonMapper.builder()`, unchecked `JacksonException`, built-in `java.time`, `ValueSerializer`, `JsonNode` renames, and every changed default (dates, durations, property order, enums, unknown properties, trailing tokens, null for primitives, empty beans, JSON views, `builderWithJackson2Defaults()`) |
| `spring-boot-app` | Spring Boot 4.1 with Jackson 3: the auto-configured `JsonMapper`, a `JsonMapperBuilderCustomizer`, `spring.jackson.use-jackson2-defaults`, and the deprecated Jackson 2 fallback (`spring-boot-jackson2` + `spring.http.converters.preferred-json-mapper=jackson2`) |
| `jackson2-app` | A small Jackson 2 code base to try the OpenRewrite recipe `org.openrewrite.java.jackson.UpgradeJackson_2_3` on |

## Versions

- Java 25 (Jackson 3 needs Java 17 or later)
- Jackson 3.2.3 (`tools.jackson.core:jackson-databind`) and Jackson 2.22.3 (`com.fasterxml.jackson.core:jackson-databind`), both with `jackson-annotations` 2.22
- Spring Boot 4.1.1 (manages Jackson 3.1.5, and Jackson 2.21.5 for `spring-boot-jackson2`)
- JUnit 6.1.3, AssertJ 3.27.7
- OpenRewrite: rewrite-maven-plugin 6.46.1, rewrite-jackson 1.29.0
- Maven 3.9+

## Run the tests

```bash
mvn test
```

30 tests: 25 in `core-changes` (`ChangedDefaultsTest` prints a 2.x vs 3.x line for each changed default) and 5 in `spring-boot-app`.

```bash
mvn -pl core-changes test -Dtest=ChangedDefaultsTest
```

## Try the OpenRewrite recipe

```bash
cd jackson2-app
mvn rewrite:dryRun     # proposed changes in target/rewrite/rewrite.patch
mvn rewrite:run        # changes the files in place
```

The recipe changes the dependencies, the imports, `JsonSerializer` to `ValueSerializer`, `JsonProcessingException` to `JacksonException`, and removes `JavaTimeModule` and the redundant `FAIL_ON_UNKNOWN_PROPERTIES` setting. It leaves `mapper.setDefaultPropertyInclusion(...)` on the mapper, which does not compile in Jackson 3, so that line has to move into `JsonMapper.builder()` by hand.
