Source code for the article https://howtodoinjava.com/spring-boot/springdoc-openapi-rest-documentation/

# Springdoc OpenAPI and Swagger UI (Recipe API)

A small REST API for recipes that documents itself with springdoc-openapi:

- one starter dependency, `springdoc-openapi-starter-webmvc-ui`
- the OpenAPI document at `/v3/api-docs` (JSON) and `/v3/api-docs.yaml`
- Swagger UI at `/swagger-ui.html`
- `@Tag`, `@Operation`, `@ApiResponse`, `@Parameter`, `@Schema` and `@Hidden`
- Bean Validation (`@NotBlank`, `@Min`) and `@RestControllerAdvice` in the generated schema
- an `OpenAPI` bean with API info and a bearer security scheme
- two `GroupedOpenApi` groups, `recipes` and `admin`
- a `prod` profile that switches both endpoints off

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Jackson 3.1.5)
- springdoc-openapi 3.1.1 (swagger-core 2.2.55, Swagger UI 5.32.14)
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

13 tests: `ApiDocsTest` (reads `/v3/api-docs` and checks the JSON), `CustomPropertiesTest` (custom paths, `packages-to-scan`, `paths-to-match` and `springdoc.override-with-generic-response=false`), `ProdProfileTest` (docs are off with the `prod` profile).

## Run the app

```bash
mvn spring-boot:run
```

Open the docs.

```bash
curl localhost:8080/v3/api-docs          # OpenAPI 3.1 JSON
curl localhost:8080/v3/api-docs.yaml     # same document as YAML
curl localhost:8080/v3/api-docs/recipes  # only the "recipes" group
curl -i localhost:8080/swagger-ui.html   # 302 to /swagger-ui/index.html
```

Open http://localhost:8080/swagger-ui.html in a browser for the Swagger UI.

Switch the docs off.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
curl -i localhost:8080/v3/api-docs       # 404
```
