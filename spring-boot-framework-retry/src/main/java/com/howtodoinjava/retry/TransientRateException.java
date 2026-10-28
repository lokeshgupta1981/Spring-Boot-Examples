package com.howtodoinjava.retry;

public class TransientRateException extends RuntimeException {
    public TransientRateException(String message) {
        super(message);
    }
}
