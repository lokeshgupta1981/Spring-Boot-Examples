package com.howtodoinjava.embabel.domain;

public sealed interface ReadingAdvice permits ReadingPlan, NoBooksFound {

  String reader();
}
