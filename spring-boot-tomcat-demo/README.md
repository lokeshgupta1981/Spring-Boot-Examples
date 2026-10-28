# Embedded Tomcat configuration

Spring Boot 4.1.2, Java 17+. Run `mvn package` and then
`java -jar target/spring-boot-tomcat-demo-1.0.0.jar`. Request
`http://127.0.0.1:9090/status`. The access log is written below
`tomcat-work/logs`.

For local HTTPS, generate a development-only PKCS12 key store with `keytool`:

```bash
keytool -genkeypair -alias local -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore local-dev.p12 -validity 30 -dname "CN=localhost" -storepass changeit -keypass changeit
```

Set `DEMO_KEYSTORE_PASSWORD=changeit` in your shell and start with
`--spring.profiles.active=ssl`. The key store is deliberately untracked.
