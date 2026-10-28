# Spring Boot annotation behavior

Spring Boot 4.1.2, Java 17+. Run `mvn package`, then
`java -jar target/spring-boot-annotations-demo-1.0.0.jar`.

- Default `/greeting`: `Hello from auto-configuration`
- `--greeting.enabled=false`: `Greeting is disabled`
- `--greeting.custom=true`: `Hello from the application`

The auto-configuration imports file registers the class explicitly. The
application configuration provides an overriding bean in the third mode.
