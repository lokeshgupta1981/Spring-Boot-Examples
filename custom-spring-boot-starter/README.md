# Custom Spring Boot starter example

This Maven reactor demonstrates a reusable reference ID formatter with Spring Boot 4.1.2.

Run `mvn test` from this directory. The feature module tests the default prefix, property binding, and application bean replacement. The sample application test verifies that the starter dependency discovers the auto-configuration through `AutoConfiguration.imports`.

Modules:

- `reference-id-spring-boot`: feature API and auto-configuration
- `reference-id-spring-boot-starter`: dependency bundle
- `sample-app`: consuming application

Run `mvn install -DskipTests` and then `mvn -f sample-app/pom.xml spring-boot:run` to see `ORD-000042` printed during startup. The first command puts the local feature and starter artifacts where the second command can resolve them.
