Source code for the article https://howtodoinjava.com/spring-cloud/aws-sqs-with-spring-cloud-aws/

# Spring Cloud AWS SQS (Invoice Queue)

A small Spring Boot app that sends invoice messages to Amazon SQS with `SqsTemplate` and receives them with `@SqsListener`:

- `InvoiceProducer`: `send()`, `sendMany()` (25 messages, split into batches of 10) and FIFO sends with a message group id
- `InvoiceListener`: `@SqsListener` with `maxConcurrentMessages = 20`, so more than 10 messages are processed at the same time
- `AuditListener`: manual acknowledgement (`acknowledgementMode = "MANUAL"` and `Acknowledgement.acknowledge()`)
- `FifoInvoiceListener`: a `.fifo` queue that keeps the order per customer
- `InvoiceExportReader`: receiving on demand with `SqsTemplate.receive()`
- `InvoiceQueueTest`: 5 tests against LocalStack started by Testcontainers (`@ServiceConnection`)

## Versions

- Spring Boot 4.0.8 (Spring Framework 7.0.9, Jackson 3.1.5, JUnit 6.0.3, Testcontainers 2.0.5)
- Spring Cloud AWS 4.2.0 (AWS SDK for Java 2.47.4)
- LocalStack 4.12.0
- Java 25, Maven 3.9+

## Run the tests

Docker must be running. Testcontainers pulls and starts `localstack/localstack:4.12.0`.

```bash
mvn test
```

5 tests: send and receive one invoice, 25 invoices processed with more than 10 in flight, receive with `SqsTemplate`, manual acknowledgement, FIFO order.

## Run the app against LocalStack

```bash
docker run -d --name localstack -p 4566:4566 localstack/localstack:4.12.0
mvn spring-boot:run
```

`application.properties` points `spring.cloud.aws.sqs.endpoint` at `http://localhost:4566`, and the queues are created on first use (`spring.cloud.aws.sqs.queue-not-found-strategy=CREATE`). On startup the app sends three invoices and the listeners log them.

```
[           main] com.howtodoinjava.sqs.StartupSender      : Sent invoice 1 to invoice-queue with message id de34536b-c578-48f6-8358-11ec91e92c79
[     invoices-1] com.howtodoinjava.sqs.InvoiceListener    : Received invoice 1 for Lokesh from web
[     invoices-2] com.howtodoinjava.sqs.InvoiceListener    : Received invoice 2 for Alex from web
[        audit-1] com.howtodoinjava.sqs.AuditListener      : Audit record written for invoice 3
[     invoices-1] com.howtodoinjava.sqs.EmailSender        : Emailed invoice 1 to Lokesh
[     invoices-2] com.howtodoinjava.sqs.EmailSender        : Emailed invoice 2 to Alex
```

List the queues inside the container.

```bash
docker exec localstack awslocal sqs list-queues
```

To run against real AWS, remove the `endpoint` property and set `spring.cloud.aws.credentials.access-key`, `spring.cloud.aws.credentials.secret-key` and `spring.cloud.aws.region.static` to your values (or use environment variables).
