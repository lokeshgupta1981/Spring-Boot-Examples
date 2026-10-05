package com.howtodoinjava.library.loan;

// Only the fields loan-service needs from book-service
public record Book(String id, String title, int copies) {
}
