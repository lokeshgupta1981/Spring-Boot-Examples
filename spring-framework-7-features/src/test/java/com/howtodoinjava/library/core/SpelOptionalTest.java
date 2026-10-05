package com.howtodoinjava.library.core;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

class SpelOptionalTest {

  @Test
  void optionalWithSafeNavigationAndElvis() {
    ExpressionParser parser = new SpelExpressionParser();
    SimpleEvaluationContext ctx = SimpleEvaluationContext.forReadOnlyDataBinding().withInstanceMethods().build();

    ctx.setVariable("shelf", Optional.of("a3"));
    String upper = parser.parseExpression("#shelf?.toUpperCase()").getValue(ctx, String.class);     // "A3"
    String label = parser.parseExpression("#shelf ?: 'none'").getValue(ctx, String.class);          // "a3"

    ctx.setVariable("shelf", Optional.empty());
    String noShelf = parser.parseExpression("#shelf?.toUpperCase()").getValue(ctx, String.class);   // null
    String fallback = parser.parseExpression("#shelf ?: 'none'").getValue(ctx, String.class);       // "none"

    System.out.println(upper + " " + label + " " + noShelf + " " + fallback);
    assertThat(upper).isEqualTo("A3");
    assertThat(label).isEqualTo("a3");
    assertThat(noShelf).isNull();
    assertThat(fallback).isEqualTo("none");
  }
}
