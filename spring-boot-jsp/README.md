Source code for the article https://howtodoinjava.com/spring-boot/spring-boot-jsp-view-example/

# Spring Boot JSP Example (Recipes)

A small Spring Boot app that renders two JSP pages with embedded Tomcat:

- `GET /recipes` lists the recipes with a JSTL `c:forEach` loop (`WEB-INF/jsp/recipes.jsp`)
- `GET /recipes/new` shows an HTML form (`WEB-INF/jsp/recipe-form.jsp`)
- `POST /recipes` adds the recipe and redirects to the list

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Tomcat 11.0.24)
- Jakarta JSTL API 3.0.2 and GlassFish JSTL implementation 3.0.1
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

2 tests in `RecipePagesTest`. They start the app on a random port and fetch the rendered HTML with `RestTestClient`.

## Run the app

```bash
mvn spring-boot:run
```

Open http://localhost:8080/recipes and http://localhost:8080/recipes/new.

## Package and run the war

```bash
mvn clean package
java -jar target/spring-boot-jsp-1.0.0.war
```

The project uses `war` packaging because Spring Boot does not support JSP pages in an executable `jar`. The same war file can also be copied to the `webapps` folder of a standalone Tomcat 11.
