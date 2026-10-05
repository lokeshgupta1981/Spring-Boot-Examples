package com.howtodoinjava.sqs;

/**
 * The message payload. Spring Cloud AWS converts it to JSON when sending and back when receiving.
 */
public record InvoiceCreated(int number, String customer, int amount) {
}
