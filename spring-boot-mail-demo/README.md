# Spring Boot mail example

Complete source for the [Spring Boot email attachment article](https://howtodoinjava.com/spring-boot2/send-email-with-attachment/). Uses Spring Boot 4.1.2 and Java 17.

`mvn test` starts GreenMail on a random local port and verifies received plain-text and MIME messages. It does not send messages to real recipients. The sample `application.properties` defaults to a local SMTP server on port 3025; set `SPRING_MAIL_HOST` and `SPRING_MAIL_PORT` for another server and add credentials in the deployment environment rather than the repository.

Some SMTP providers require authentication and STARTTLS. Add these properties only when that provider specifies them:

```properties
spring.mail.properties[mail.smtp.auth]=true
spring.mail.properties[mail.smtp.starttls.enable]=true
```

Set `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, and `APP_MAIL_FROM` in the deployment environment. Use a sender address the provider authorizes.
