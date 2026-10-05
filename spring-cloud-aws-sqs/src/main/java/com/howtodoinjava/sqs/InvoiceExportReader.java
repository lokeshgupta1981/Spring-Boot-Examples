package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.time.Duration;
import java.util.Optional;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/** Reads the export queue on demand with SqsTemplate instead of a listener. */
@Component
public class InvoiceExportReader {

  private final SqsTemplate sqsTemplate;

  public InvoiceExportReader(SqsTemplate sqsTemplate) {
    this.sqsTemplate = sqsTemplate;
  }

  /** Waits up to 5 seconds for one message, then deletes it from the queue. */
  public Optional<InvoiceCreated> readNext() {
    Optional<Message<InvoiceCreated>> message = sqsTemplate.receive(from -> from
        .queue(Queues.EXPORT)
        .pollTimeout(Duration.ofSeconds(5)), InvoiceCreated.class);
    return message.map(Message::getPayload);
  }
}
