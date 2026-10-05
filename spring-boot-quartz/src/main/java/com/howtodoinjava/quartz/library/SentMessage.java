package com.howtodoinjava.quartz.library;

/** A reminder or fine notice. Instead of sending an email, we store it in the sent_message table. */
public record SentMessage(long loanId, String kind, String recipient, String text) {
}
