# Spring Boot MockitoBean example

Complete source for the [Spring Boot MockitoBean article](https://howtodoinjava.com/spring-boot2/testing/spring-mockbean-annotation/). Uses Spring Boot 4.1.2 and Java 17.

Run `mvn test`. Four tests compare plain Mockito, MVC slices that create a mock bean by field or by type-level annotation, and a full application context that replaces an existing bean with `@MockitoBean(enforceOverride = true)`.
