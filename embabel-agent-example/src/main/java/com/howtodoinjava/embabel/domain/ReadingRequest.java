package com.howtodoinjava.embabel.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ReadingRequest(
    @JsonPropertyDescription("first name of the reader") String reader,
    @JsonPropertyDescription("book genre in lowercase, for example fantasy") String genre,
    @JsonPropertyDescription("number of days the reader has") int days) {
}
