package com.howtodoinjava.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FifoInvoiceListener {

  private static final Logger log = LoggerFactory.getLogger(FifoInvoiceListener.class);

  private final List<Integer> numbersInOrder = new CopyOnWriteArrayList<>();

  // A queue name ending with .fifo is set up as a FIFO queue by the framework.
  @SqsListener(value = Queues.INVOICES_FIFO, id = "invoices-fifo")
  public void onInvoiceCreated(InvoiceCreated invoice) {
    log.info("FIFO invoice {} for {}", invoice.number(), invoice.customer());
    numbersInOrder.add(invoice.number());
  }

  public List<Integer> numbersInOrder() {
    return numbersInOrder;
  }
}
