# Gson with Spring Boot 4.1.2

Requires Java 17+ and Maven.

```bash
mvn package
java -jar target/spring-boot-gson-demo-1.0.0.jar
```

With no active profile, `/catalog/item` uses Jackson and `/catalog/manual`
uses the injected `Gson` bean. Run with `--spring.profiles.active=gson` to make
the MVC JSON response use Gson too. The `@SerializedName` annotation visibly
changes `code` to `item_code` in Gson output.
