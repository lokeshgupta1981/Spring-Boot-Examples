# RestTemplate with Apache HttpClient 5

Spring Boot 4.1.2 example of a `RestTemplate` backed by an explicitly configured Apache HttpClient 5 connection pool. The configuration distinguishes the pool wait, connection establishment and response timeouts.

Run `mvn test`. The test starts a local HTTP server, checks the actual request factory and verifies JSON response mapping.
