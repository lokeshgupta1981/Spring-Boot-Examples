Source code for the article https://howtodoinjava.com/spring-security/configurations-without-websecurityconfigureradapter/

# Spring Security without WebSecurityConfigurerAdapter

A small recipe API that shows the Spring Security 7 configuration style, where every piece that used to be an overridden `configure(...)` method of `WebSecurityConfigurerAdapter` is a bean:

- `SecurityFilterChain` bean with the lambda DSL (`authorizeHttpRequests`, `requestMatchers`, `csrf`, `httpBasic`, `formLogin`)
- `WebSecurityCustomizer` bean in place of `configure(WebSecurity)`
- `UserDetailsService` + `PasswordEncoder` beans in place of `inMemoryAuthentication()` and `User.withDefaultPasswordEncoder()`
- `AuthenticationManager` bean in place of `authenticationManagerBean()`
- `@EnableMethodSecurity` in place of `@EnableGlobalMethodSecurity(prePostEnabled = true)`

## Versions

- Spring Boot 4.1.1 (Spring Framework 7.0.9, Spring Security 7.1.1, JUnit 6.0.3)
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

8 tests in `SecurityConfigTest` (MockMvcTester): a public URL, a protected URL without and with HTTP Basic, an admin URL for the USER and the ADMIN role, method security on the service, and an ignored static path.

## Run the app

```bash
mvn spring-boot:run
```

Call it. Users are `ravi` (USER) and `admin` (USER, ADMIN), both with the password `pass`.

```bash
curl localhost:8080/recipes                             # ["pasta","salad","soup"]
curl localhost:8080/recipes/mine                        # 401
curl -u ravi:pass localhost:8080/recipes/mine           # ["omelette"]
curl -u ravi:pass localhost:8080/admin/recipes          # 403
curl -u admin:pass localhost:8080/admin/recipes         # all recipes of all users
curl -u ravi:pass localhost:8080/recipes/secret         # 403 (method security)
```
