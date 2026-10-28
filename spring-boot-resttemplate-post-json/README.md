# RestTemplate JSON POST example

Spring Boot 4.1.2 / Java 17+ companion to the [HowToDoInJava tutorial](https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-post-json-example/).

`ShipmentController` accepts a shipment creation record, returns HTTP 201 with a mapped body and `Location`, and echoes an optional request ID. `ShipmentClient` demonstrates `postForEntity`, explicit `HttpEntity` headers, `postForObject`, and `postForLocation`.

Run `mvn test`. Five integration tests cover 201/body/location, headers with a non-ASCII destination, the two alternative return shapes, and an invalid request yielding HTTP 400.
