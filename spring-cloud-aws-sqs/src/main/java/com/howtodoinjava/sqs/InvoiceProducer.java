package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProducer {

  private final SqsTemplate sqsTemplate;

  public InvoiceProducer(SqsTemplate sqsTemplate) {
    this.sqsTemplate = sqsTemplate;
  }

  /** Sends one invoice to the standard queue with a custom header. */
  public SendResult<InvoiceCreated> send(InvoiceCreated invoice) {
    return sqsTemplate.send(to -> to
        .queue(Queues.INVOICES)
        .payload(invoice)
        .header("source", "web"));
  }

  /** Sends many invoices in batches of 10 (the SQS limit per request). */
  public SendResult.Batch<InvoiceCreated> sendMany(List<InvoiceCreated> invoices) {
    List<Message<InvoiceCreated>> messages = invoices.stream()
        .map(invoice -> MessageBuilder.withPayload(invoice).setHeader("source", "import").build())
        .toList();
    return sqsTemplate.sendMany(Queues.INVOICES, messages);
  }

  /** Sends one invoice to the queue with manual acknowledgement. */
  public SendResult<InvoiceCreated> sendForAudit(InvoiceCreated invoice) {
    return sqsTemplate.send(Queues.AUDIT, invoice);
  }

  /** Sends one invoice to the export queue, which has no listener. */
  public SendResult<InvoiceCreated> sendForExport(InvoiceCreated invoice) {
    return sqsTemplate.send(Queues.EXPORT, invoice);
  }

  /** Sends one invoice to the FIFO queue. Messages of one customer keep their order. */
  public SendResult<InvoiceCreated> sendFifo(InvoiceCreated invoice) {
    return sqsTemplate.send(to -> to
        .queue(Queues.INVOICES_FIFO)
        .payload(invoice)
        .messageGroupId(invoice.customer())
        .messageDeduplicationId(String.valueOf(invoice.number())));
  }
}
