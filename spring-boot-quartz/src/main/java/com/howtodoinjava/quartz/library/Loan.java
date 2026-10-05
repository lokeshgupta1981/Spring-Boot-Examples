package com.howtodoinjava.quartz.library;

import java.time.LocalDate;

public record Loan(long id, String member, String book, LocalDate dueDate, LocalDate returnedOn) {
}
