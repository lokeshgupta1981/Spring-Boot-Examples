Source code for the article https://howtodoinjava.com/?p=44240

# Spring Security 7 (Expense API)

A small expense API that shows the main Spring Security 7 changes in one app:

- the lambda DSL only (no `and()`, no `authorizeRequests()`, `HttpSecurity.build()` without `throws Exception`)
- `PathPatternRequestMatcher` in place of `AntPathRequestMatcher` and `MvcRequestMatcher`
- multi-factor authentication with `@EnableMultiFactorAuthentication` and `AuthorizationManagerFactories.multiFactor()`
- one-time token login as the second factor
- `hasAllRoles(...)` for rules that need every listed role
- SPA-friendly CSRF with `csrf.spa()`
- relative login redirects
- Jackson 3 serialization of `Authentication` with `SecurityJacksonModules`
- `Authentication.toBuilder()`

## Versions

- Spring Boot 4.1.1 (Spring Security 7.1.1, Spring Framework 7.0.9, Jackson 3.1.5)
- Java 25
- Maven 3.9+

## Run the tests

```bash
mvn test
```

14 tests: `SecurityRulesTest` (MockMvcTester against the filter chain), `ApiChangesTest` (request matcher, Jackson 3, `toBuilder()`), `RemovedApisTest` (classes and methods removed in 7.0).

## Run the app

```bash
mvn spring-boot:run
```

Users: `lokesh` / `secret` (role USER) and `anna` / `secret` (role ADMIN).

```bash
curl localhost:8080/public/currencies                    # ["USD","EUR"]
curl -u lokesh:secret localhost:8080/expenses            # {"taxi":25,"lunch":12}
curl -i localhost:8080/expenses -H "Accept: text/html"   # 302, Location: http://localhost:8080/login
```

Spring Security 7 sends the relative URL `/login`, and embedded Tomcat turns it into an absolute URL. Start the app with `--server.tomcat.use-relative-redirects=true` to see `Location: /login`. The MockMvc test `loginRedirectIsRelative` checks the value Spring Security sends.

Open http://localhost:8080/reports/monthly in a browser and log in as `anna`. After the password, Spring Security redirects to `/login?factor.type=ott&factor.reason=missing` and asks for a one-time token. The demo does not send emails. When you request the token, the app writes the login link to the log:

```
Login link for anna: http://localhost:8080/login/ott?token=...
```

Open the link, submit the token, and the report opens: `{"total":37}`.
