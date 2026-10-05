package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class InvoiceListener {

  private static final Logger log = LoggerFactory.getLogger(InvoiceListener.class);

  private final EmailSender emailSender;
  private final List<InvoiceCreated> received = new CopyOnWriteArrayList<>();

  public InvoiceListener(EmailSender emailSender) {
    this.emailSender = emailSender;
  }

  // Up to 20 messages of this queue are processed at the same time (default 10).
  @SqsListener(value = Queues.INVOICES, id = "invoices", maxConcurrentMessages = "20", maxMessagesPerPoll = "10")
  public void onInvoiceCreated(InvoiceCreated invoice, @Header("source") String source) {
    log.info("Received invoice {} for {} from {}", invoice.number(), invoice.customer(), source);
    emailSender.send(invoice);
    received.add(invoice);
  }

  public List<InvoiceCreated> received() {
    return received;
  }
}
