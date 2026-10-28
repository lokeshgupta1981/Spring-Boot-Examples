# Read a packaged classpath resource

The sample accompanies the HowToDoInJava article about reading files from `src/main/resources`. It uses Spring Boot 4.1.2 and Java 17 or later.

Run `mvn spring-boot:run` to read the template from the development classpath. `Physical file available` prints `true`.

Run `mvn package` and then `java -jar target/spring-boot-resource-demo-1.0.0.jar` to read the same template from an executable JAR. All three stream-based reads still succeed, while `Physical file available` prints `false`. This demonstrates why an input stream is the portable choice for a packaged resource.
