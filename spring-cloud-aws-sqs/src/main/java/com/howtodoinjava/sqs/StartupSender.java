package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.operations.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Sends three invoices when the app starts, so "mvn spring-boot:run" shows the listener working. */
@Component
@ConditionalOnProperty(name = "app.send-on-startup", havingValue = "true")
public class StartupSender implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(StartupSender.class);

  private final InvoiceProducer producer;

  public StartupSender(InvoiceProducer producer) {
    this.producer = producer;
  }

  @Override
  public void run(ApplicationArguments args) {
    SendResult<InvoiceCreated> result = producer.send(new InvoiceCreated(1, "Lokesh", 120));
    log.info("Sent invoice 1 to {} with message id {}", result.endpoint(), result.messageId());
    producer.send(new InvoiceCreated(2, "Alex", 80));
    producer.sendForAudit(new InvoiceCreated(3, "Lokesh", 200));
  }
}
