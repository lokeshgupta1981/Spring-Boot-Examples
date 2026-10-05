package com.howtodoinjava.sqs;

public final class Queues {

  public static final String INVOICES = "invoice-queue";
  public static final String AUDIT = "invoice-audit-queue";
  public static final String INVOICES_FIFO = "invoice-queue.fifo";
  public static final String EXPORT = "invoice-export-queue";

  private Queues() {
  }
}
