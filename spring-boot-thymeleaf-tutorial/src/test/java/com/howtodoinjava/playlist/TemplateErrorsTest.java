package com.howtodoinjava.playlist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.springframework.expression.spel.SpelEvaluationException;

@WebMvcTest(BrokenPagesController.class)
class TemplateErrorsTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void missingTemplateThrowsTemplateInputException() {
    MvcTestResult result = mvc.get().uri("/broken/missing").exchange();
    Throwable root = rootCause(result.getUnresolvedException());
    System.out.println("MISSING: " + root.getClass().getName() + ": " + root.getMessage());
    assertThat(root).isInstanceOf(TemplateInputException.class)
        .hasMessageContaining("Error resolving template [songs/lists]");
  }

  @Test
  void typoInPropertyThrowsSpelEvaluationException() {
    MvcTestResult result = mvc.get().uri("/broken/typo").exchange();
    Throwable ex = result.getUnresolvedException();
    Throwable t = ex;
    while (t != null) {
      System.out.println("TYPO: " + t.getClass().getName() + ": " + t.getMessage());
      t = t.getCause();
    }
    assertThat(rootCause(ex)).isInstanceOf(SpelEvaluationException.class)
        .hasMessageContaining("EL1008E");
  }

  @Test
  void selectionExpressionInThObjectIsRejected() {
    MvcTestResult result = mvc.get().uri("/broken/selection").exchange();
    Throwable root = rootCause(result.getUnresolvedException());
    System.out.println("SELECTION: " + root.getClass().getName() + ": " + root.getMessage());
    assertThat(root).isInstanceOf(TemplateProcessingException.class)
        .hasMessageContaining("which is not valid: only variable expressions");
  }

  @Test
  void loopVariableInsideThObjectIsNotFound() {
    MvcTestResult result = mvc.get().uri("/broken/loop").exchange();
    Throwable root = rootCause(result.getUnresolvedException());
    System.out.println("LOOP: " + root.getClass().getName() + ": " + root.getMessage());
    assertThat(root).isInstanceOf(SpelEvaluationException.class)
        .hasMessageContaining("Property or field 'tag' cannot be found");
  }

  private static Throwable rootCause(Throwable t) {
    Throwable c = t;
    while (c.getCause() != null && c.getCause() != c) {
      c = c.getCause();
    }
    return c;
  }
}
