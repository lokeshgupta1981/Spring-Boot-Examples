package com.howtodoinjava.quartz.library;

import java.math.BigDecimal;

public record Fine(long loanId, String member, String book, long daysLate, BigDecimal amount) {
}
