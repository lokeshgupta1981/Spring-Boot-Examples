package com.howtodoinjava.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.awspring.cloud.sqs.operations.SendResult;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "app.send-on-startup=false")
@Testcontainers
class InvoiceQueueTest {

  // @ServiceConnection points every AWS client of the app at this container.
  @Container
  @ServiceConnection
  static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.12.0"));

  @Autowired
  InvoiceProducer producer;

  @Autowired
  InvoiceListener invoiceListener;

  @Autowired
  AuditListener auditListener;

  @Autowired
  FifoInvoiceListener fifoListener;

  @Autowired
  EmailSender emailSender;

  @Autowired
  InvoiceExportReader exportReader;

  @Test
  void sendsAndReceivesOneInvoice() {
    InvoiceCreated invoice = new InvoiceCreated(1, "Lokesh", 120);

    SendResult<InvoiceCreated> result = producer.send(invoice);

    assertThat(result.messageId()).isNotNull();
    assertThat(result.endpoint()).isEqualTo(Queues.INVOICES);
    await().atMost(Duration.ofSeconds(10))
        .untilAsserted(() -> assertThat(invoiceListener.received()).contains(invoice));
  }

  @Test
  void processesMoreThanTenInvoicesAtTheSameTime() {
    List<InvoiceCreated> invoices = IntStream.rangeClosed(101, 125)
        .mapToObj(number -> new InvoiceCreated(number, "Customer " + number, 10))
        .toList();

    SendResult.Batch<InvoiceCreated> batch = producer.sendMany(invoices);

    assertThat(batch.successful()).hasSize(25);
    assertThat(batch.failed()).isEmpty();
    await().atMost(Duration.ofSeconds(20))
        .untilAsserted(() -> assertThat(invoiceListener.received()).containsAll(invoices));
    assertThat(emailSender.maxInFlight()).isGreaterThan(10);
  }

  @Test
  void receivesWithSqsTemplate() {
    InvoiceCreated invoice = new InvoiceCreated(401, "Alex", 90);
    producer.sendForExport(invoice);

    Optional<InvoiceCreated> first = exportReader.readNext();
    Optional<InvoiceCreated> second = exportReader.readNext();

    assertThat(first).contains(invoice);
    assertThat(second).isEmpty();   // the first receive() deleted the message
  }

  @Test
  void acknowledgesAuditMessageManually() {
    InvoiceCreated invoice = new InvoiceCreated(201, "Alex", 80);

    producer.sendForAudit(invoice);

    await().atMost(Duration.ofSeconds(10))
        .untilAsserted(() -> assertThat(auditListener.audited()).contains(invoice));
  }

  @Test
  void fifoQueueKeepsTheOrderOfOneCustomer() {
    List<InvoiceCreated> invoices = IntStream.rangeClosed(301, 305)
        .mapToObj(number -> new InvoiceCreated(number, "Lokesh", 50))
        .toList();

    invoices.forEach(producer::sendFifo);

    await().atMost(Duration.ofSeconds(20))
        .untilAsserted(() -> assertThat(fifoListener.numbersInOrder()).hasSize(5));
    assertThat(fifoListener.numbersInOrder()).containsExactly(301, 302, 303, 304, 305);
  }
}
