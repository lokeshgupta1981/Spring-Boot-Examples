Source code for the article https://howtodoinjava.com/?p=44107

# Spring Authorization Server with Spring Boot 4.1

Two Spring Boot applications in one Maven build:

| Module | Port | What it does |
|---|---|---|
| auth-server | 9000 | Spring Authorization Server. Issues JWT access tokens to two clients: *recipe-cli* (client_credentials) and *recipe-web* (authorization_code + PKCE, public client). User: lokesh / password (role CHEF). |
| resource-server | 8082 | Recipe API. Validates the JWTs with the keys from http://localhost:9000/oauth2/jwks. |

## Versions

- Java 25 (Java 17 or later works)
- Spring Boot 4.1.1
- Spring Security 7.1.1 (includes Spring Authorization Server)
- Starters: spring-boot-starter-security-oauth2-authorization-server, spring-boot-starter-security-oauth2-resource-server
- H2 2.4.240 (only for the "jdbc" profile)

## Build and test

```bash
mvn test
```

## Run

```bash
# terminal 1
mvn -pl auth-server spring-boot:run

# terminal 1 (alternative): store clients in H2 with JdbcRegisteredClientRepository
mvn -pl auth-server spring-boot:run -Dspring-boot.run.profiles=jdbc

# terminal 2
mvn -pl resource-server spring-boot:run
```

## Try it

```bash
# client_credentials: token, decoded payload, API call
./scripts/client-credentials.sh

# authorization_code + PKCE with curl only (cookie jar + login form)
./scripts/pkce-flow.sh
```

Endpoints of the authorization server:

- http://localhost:9000/.well-known/openid-configuration
- http://localhost:9000/oauth2/jwks
- http://localhost:9000/oauth2/token
- http://localhost:9000/oauth2/authorize

The signing keys are generated at startup, so tokens become invalid after a restart of auth-server.
A real server loads its keys from a keystore or a secrets vault.
