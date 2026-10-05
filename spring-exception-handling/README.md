Source code for the article https://howtodoinjava.com/spring-core/spring-exceptionhandler-annotation/

# Spring @ExceptionHandler and @ControllerAdvice (Bookings API)

A small bookings REST API that shows every way to handle exceptions in Spring MVC:

- a local `@ExceptionHandler` method inside `BookingController`
- a global `@RestControllerAdvice` class (`GlobalExceptionHandler`) that extends `ResponseEntityExceptionHandler`
- `ProblemDetail` (RFC 9457) error bodies with an extra property
- validation errors (`MethodArgumentNotValidException`) listed per field
- `@ResponseStatus` on an exception class (`BookingCancelledException`)
- a catch-all handler that hides internal error messages
- `MockMvcTester` tests for each case

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, JUnit 6.0.3)
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

8 tests in `BookingControllerTest`.

## Run the app

```bash
mvn spring-boot:run
```

Call it.

```bash
curl -i localhost:8080/bookings/1            # 200 {"id":1,"guest":"Lokesh","nights":2}
curl -i localhost:8080/bookings/5            # 404 handled by the controller's own @ExceptionHandler
curl -i localhost:8080/bookings/0            # 400 from the controller handler that names two exception types
curl -i localhost:8080/guests/5/name         # 404 handled by GlobalExceptionHandler
curl -i localhost:8080/bookings/99           # 410 from @ResponseStatus on BookingCancelledException
curl -i -X POST localhost:8080/bookings/1/rebook   # 500 from the catch-all handler
curl -i -X DELETE localhost:8080/bookings/1  # 405 from ResponseEntityExceptionHandler
curl -i -X POST -H "Content-Type: application/json" -d '{"guest":"","nights":0}' localhost:8080/bookings   # 400 validation errors
```

Start with `--spring.profiles.active=no-handlers` to switch the global handler off and see Spring Boot's default error JSON.
