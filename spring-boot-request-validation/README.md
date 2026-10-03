# Spring Boot Request Validation

Source code for the article [Spring Boot Request Validation for Body and Parameters](https://howtodoinjava.com/spring-rest/request-body-parameter-validation/).

A small yoga studio booking API that validates:

- the request body with `@Valid @RequestBody` (`BookingController`, `BookingRequest`)
- `@PathVariable` and `@RequestParam` values with constraints directly on the parameters (built-in method validation, Spring Framework 6.1+)
- nested objects and list elements (`GroupBookingRequest`, `List<@Valid Attendee>`, `List<@Valid BookingRequest>` body)
- validation groups for create and update (`YogaClassRequest`, `OnCreate`, `OnUpdate`)
- a custom constraint `@OpenDay` (`OpenDayValidator`)

`ApiExceptionHandler` extends `ResponseEntityExceptionHandler` and returns a `ProblemDetail` body with an `errors` map for both `MethodArgumentNotValidException` and `HandlerMethodValidationException`.

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Framework 7.0.9, Hibernate Validator 9.1.3.Final, Jakarta Validation 3.1.1, Jackson 3.1.5)

## Run

```bash
mvn test
mvn spring-boot:run
curl -i -X POST localhost:8080/bookings -H "Content-Type: application/json" -d '{"name":"","email":"lokesh-at-example","seats":9}'
```

Expected test result: `Tests run: 16, Failures: 0, Errors: 0, Skipped: 0`.
