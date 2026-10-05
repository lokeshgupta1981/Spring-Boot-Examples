package com.howtodoinjava.jackson;

import com.howtodoinjava.jackson.model.Recipe;
import com.howtodoinjava.jackson.v2.RecipeJson2;
import com.howtodoinjava.jackson.v3.RecipeJson3;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Checked JsonProcessingException (Jackson 2) vs unchecked JacksonException (Jackson 3). */
class ExceptionTest {

  @Test
  void jackson2ExceptionIsChecked() {
    assertThat(IOException.class)
        .isAssignableFrom(com.fasterxml.jackson.core.JsonProcessingException.class);
    assertThat(new RecipeJson2().read("{bad json")).isEmpty();
  }

  @Test
  void jackson3ExceptionIsUnchecked() {
    assertThat(RuntimeException.class).isAssignableFrom(tools.jackson.core.JacksonException.class);
    assertThat(IOException.class.isAssignableFrom(tools.jackson.core.JacksonException.class)).isFalse();
    assertThat(new RecipeJson3().read("{bad json")).isEmpty();
  }

  @Test
  void jackson3ExceptionSubtypes() {
    tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();

    assertThatThrownBy(() -> mapper.readValue("{bad json", Recipe.class))
        .isInstanceOf(tools.jackson.core.exc.StreamReadException.class)
        .isInstanceOf(tools.jackson.core.JacksonException.class);

    assertThatThrownBy(() -> mapper.readValue("{\"servings\":\"four\"}", Recipe.class))
        .isInstanceOf(tools.jackson.databind.DatabindException.class);
  }
}
