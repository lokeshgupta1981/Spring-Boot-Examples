package com.howtodoinjava.boot4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = {
    "management.tracing.export.enabled=false",
    "spring.jackson.factory.constraints.read.max-nesting-depth=2"})
class JacksonThreeTest {

  @Autowired
  JsonMapper jsonMapper;

  @Test
  void writesDatesAsIsoStrings() {
    PantryItem apple = new PantryItem("apple", 5, LocalDate.of(2026, 10, 12));
    String json = jsonMapper.writeValueAsString(apple);
    System.out.println("Jackson 3 JSON: " + json);
    assertThat(json).isEqualTo("{\"name\":\"apple\",\"quantity\":5,\"bestBefore\":\"2026-10-12\"}");
  }

  @Test
  void readsWithoutCheckedExceptions() {
    PantryItem banana = jsonMapper.readValue(
        "{\"name\":\"banana\",\"quantity\":3,\"bestBefore\":\"2026-10-08\"}", PantryItem.class);
    assertThat(banana.quantity()).isEqualTo(3);
  }

  @Test
  void badJsonThrowsUncheckedJacksonException() {
    assertThatThrownBy(() -> jsonMapper.readValue("{\"name\":", PantryItem.class))
        .isInstanceOf(JacksonException.class)
        .isInstanceOf(RuntimeException.class)
        .satisfies(e -> System.out.println("Bad JSON -> " + e.getClass().getName()));
  }

  @Test
  void badJsonCanBeCaughtAsJacksonException() {
    String json = "{\"name\":";
    PantryItem item;
    try {
      item = jsonMapper.readValue(json, PantryItem.class);
    } catch (JacksonException e) {    // unchecked, so the catch block is optional
      System.out.println("Caught -> " + e.getOriginalMessage());
      item = null;
    }
    assertThat(item).isNull();
  }

  @Test
  void factoryConstraintLimitsNesting() {
    assertThatThrownBy(() -> jsonMapper.readTree("{\"a\":{\"b\":{\"c\":1}}}"))
        .isInstanceOf(StreamConstraintsException.class)
        .satisfies(e -> System.out.println("Too deep -> " + e.getMessage().lines().findFirst().orElse("")));
  }
}
