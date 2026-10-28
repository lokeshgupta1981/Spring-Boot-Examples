# Spring Boot configuration demo

Runnable source for the Spring Boot starter parent and `@SpringBootApplication` tutorials. Uses Spring Boot 4.1.2 and Java 17 or later.

```bash
mvn package
java -jar target/boot-configuration-demo-1.0.0.jar
```

Request `http://localhost:8080/catalog/status` to receive `Catalog ready`.

To see the condition evaluation report, run the JAR with `--debug`. To see a non-web run while keeping the controller in the context, run:

```bash
java -jar target/boot-configuration-demo-1.0.0.jar --spring.main.web-application-type=none --debug
```
