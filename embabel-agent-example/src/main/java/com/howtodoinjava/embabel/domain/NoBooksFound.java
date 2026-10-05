package com.howtodoinjava.embabel.domain;

public record NoBooksFound(String reader, String genre) implements ReadingAdvice {
}
