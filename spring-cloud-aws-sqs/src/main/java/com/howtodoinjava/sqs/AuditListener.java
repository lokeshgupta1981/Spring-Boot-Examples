package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

  private static final Logger log = LoggerFactory.getLogger(AuditListener.class);

  private final List<InvoiceCreated> audited = new CopyOnWriteArrayList<>();

  // MANUAL: the message stays in the queue until we call acknowledge().
  @SqsListener(value = Queues.AUDIT, id = "audit", acknowledgementMode = "MANUAL")
  public void onInvoiceCreated(Message<InvoiceCreated> message, Acknowledgement ack) {
    InvoiceCreated invoice = message.getPayload();
    log.info("Audit record written for invoice {}", invoice.number());
    audited.add(invoice);
    ack.acknowledge();   // deletes the message from the queue
  }

  public List<InvoiceCreated> audited() {
    return audited;
  }
}
