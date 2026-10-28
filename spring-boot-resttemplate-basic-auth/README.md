# RestTemplate Basic authentication example

Spring Boot 4.1.2 / Java 17+ sample used by the [HowToDoInJava article](https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-basicauth-example/).

`ShipmentController` serves `/shipments/{id}/status`. `SecurityConfiguration` protects it with HTTP Basic. `ShipmentClient` shows both a template configured for one account and a header configured for one request.

Run `mvn test`. Four tests start a server on a random port and verify a successful fixed-credential request, a successful per-request header, wrong credentials and no credentials. The example account is only for local tests; use a proper password store, externally supplied credentials and HTTPS outside this example.
