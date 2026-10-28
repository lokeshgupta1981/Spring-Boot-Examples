# RestTemplate request and response logging

This Spring Boot 4.1.2 example shows a metadata-only `ClientHttpRequestInterceptor` and an optional response-body interceptor. The latter requires `BufferingClientHttpRequestFactory` and is appropriate only for bounded, non-sensitive diagnostic responses.

Run `mvn test`. The tests start a local HTTP server, check the request log, and verify that response JSON is still converted after the diagnostic interceptor reads it.
