package com.howtodoinjava.library.gateway;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Bound from library.book-service and library.loan-service,
// or from the LIBRARY_BOOKSERVICE / LIBRARY_LOANSERVICE environment variables
@ConfigurationProperties("library")
public record LibraryServices(URI bookService, URI loanService) {
}
