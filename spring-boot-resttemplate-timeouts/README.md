# RestTemplate connect and read timeouts

Spring Boot 4.1.2 / Java 17+ companion to the [HowToDoInJava tutorial](https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-timeout-example/).

`ShipmentClient` uses an explicit `SimpleClientHttpRequestFactory`, a two-second connection timeout and a one-second read timeout. Run `mvn test`: one integration test gets an immediate shipment status and the other confirms that a three-second response delay produces `ResourceAccessException` caused by `SocketTimeoutException`. The delayed endpoint demonstrates the read timeout; it does not test connection establishment or a total request deadline.
