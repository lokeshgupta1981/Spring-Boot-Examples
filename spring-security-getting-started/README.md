# Spring Security Getting Started

Source code for the article [Spring Security Tutorial](https://howtodoinjava.com/spring-security/spring-security-tutorial/).

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Security 7.1.1)

## What the example shows

- `SecurityConfig`: a `SecurityFilterChain` with public, authenticated and ADMIN-only URLs, form login and HTTP Basic,
  two in-memory users and a delegating (bcrypt) `PasswordEncoder`
- `PlaylistController`: `/public/top-songs`, `/playlists` (GET and POST) and `/admin/report`
- `src/test/.../defaults`: a test-only application with NO security code, showing Spring Boot's defaults
  (generated password, 401 vs redirect to `/login`, security headers, `spring.security.user.*` properties)
- Tests: status codes for each rule over MockMvc and real HTTP, `@WithMockUser`, `csrf()`, `formLogin()`,
  bcrypt hashes and the filter order of the chain

## Run

```bash
mvn test               # 24 tests
mvn spring-boot:run    # the playlist API on http://localhost:8080 (users lokesh/password and admin/admin123)

# the default behavior (prints "Using generated security password: ...")
mvn spring-boot:test-run -Dspring-boot.run.main-class=com.howtodoinjava.defaults.DefaultSecurityApplication
```
