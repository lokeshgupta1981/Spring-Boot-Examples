package com.howtodoinjava.referenceid;

public class ReferenceIdFormatter {
    private final String prefix;

    public ReferenceIdFormatter(String prefix) {
        this.prefix = prefix;
    }

    public String format(long orderId) {
        if (orderId < 0) {
            throw new IllegalArgumentException("orderId must not be negative");
        }
        return "%s-%06d".formatted(prefix, orderId);
    }
}
