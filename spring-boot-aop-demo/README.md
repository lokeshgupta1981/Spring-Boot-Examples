# Spring Boot AOP example

Complete source for the [Spring Boot AOP article](https://howtodoinjava.com/spring-boot2/aop-aspectj/). Uses Spring Boot 4.1.2 and Java 17.

Run with `mvn test`. The tests show that calls through the Spring proxy run `@Around` advice and self-invocation does not. They also show that the `finally` block records failed calls.
