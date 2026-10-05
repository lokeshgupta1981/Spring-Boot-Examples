package com.howtodoinjava.jackson;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** JsonNode: TextNode becomes StringNode, asText() gets the new name asString(). */
class TreeModelTest {

  static final String JSON = "{\"name\":\"pancakes\",\"servings\":4}";

  @Test
  void jackson2Tree() throws Exception {
    com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(JSON);

    String name = root.get("name").asText();
    assertThat(name).isEqualTo("pancakes");
    assertThat(root.get("name")).isInstanceOf(com.fasterxml.jackson.databind.node.TextNode.class);
  }

  @Test
  void jackson3Tree() {
    tools.jackson.databind.JsonNode root = tools.jackson.databind.json.JsonMapper.builder().build().readTree(JSON);

    String name = root.get("name").asString();
    int servings = root.get("servings").asInt();
    assertThat(name).isEqualTo("pancakes");
    assertThat(servings).isEqualTo(4);
    assertThat(root.get("name")).isInstanceOf(tools.jackson.databind.node.StringNode.class);
    assertThat(root.properties()).hasSize(2);
  }
}
