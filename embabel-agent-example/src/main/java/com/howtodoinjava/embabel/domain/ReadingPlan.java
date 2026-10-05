package com.howtodoinjava.embabel.domain;

public record ReadingPlan(String reader, String title, int pagesPerDay, String note)
    implements ReadingAdvice {
}
