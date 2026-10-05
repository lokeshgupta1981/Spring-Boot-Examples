package com.howtodoinjava.jackson;

import com.howtodoinjava.jackson.model.Difficulty;
import com.howtodoinjava.jackson.model.Dish;
import com.howtodoinjava.jackson.model.Empty;
import com.howtodoinjava.jackson.model.Ingredient;
import com.howtodoinjava.jackson.model.Portion;
import com.howtodoinjava.jackson.model.Recipe;
import com.howtodoinjava.jackson.model.Review;
import com.howtodoinjava.jackson.v2.RecipeJson2;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Every default that changed between Jackson 2.22 and Jackson 3.2, run on both versions. */
class ChangedDefaultsTest {

  // Jackson 2 with JavaTimeModule, as most Jackson 2 apps had it
  final com.fasterxml.jackson.databind.json.JsonMapper v2 = RecipeJson2.builtMapper();
  final tools.jackson.databind.json.JsonMapper v3 = tools.jackson.databind.json.JsonMapper.builder().build();

  @Test
  void datesAsIsoStrings() throws Exception {
    LocalDate day = LocalDate.of(2026, 10, 5);
    String json2 = v2.writeValueAsString(new Recipe("pancakes", 4, day));
    String json3 = v3.writeValueAsString(new Recipe("pancakes", 4, day));
    print("WRITE_DATES_AS_TIMESTAMPS", json2, json3);

    assertThat(json2).isEqualTo("{\"name\":\"pancakes\",\"servings\":4,\"createdOn\":[2026,10,5]}");
    assertThat(json3).isEqualTo("{\"name\":\"pancakes\",\"servings\":4,\"createdOn\":\"2026-10-05\"}");
  }

  @Test
  void durationsAsIsoStrings() throws Exception {
    String json2 = v2.writeValueAsString(Map.of("bake", Duration.ofMinutes(25)));
    String json3 = v3.writeValueAsString(Map.of("bake", Duration.ofMinutes(25)));
    print("WRITE_DURATIONS_AS_TIMESTAMPS", json2, json3);

    assertThat(json2).isEqualTo("{\"bake\":1500.000000000}");
    assertThat(json3).isEqualTo("{\"bake\":\"PT25M\"}");
  }

  @Test
  void propertiesSortedAlphabetically() throws Exception {
    String json2 = v2.writeValueAsString(Ingredient.of("flour", 250, true));
    String json3 = v3.writeValueAsString(Ingredient.of("flour", 250, true));
    print("SORT_PROPERTIES_ALPHABETICALLY", json2, json3);

    assertThat(json2).isEqualTo("{\"name\":\"flour\",\"grams\":250,\"vegan\":true}");
    assertThat(json3).isEqualTo("{\"grams\":250,\"name\":\"flour\",\"vegan\":true}");
  }

  @Test
  void recordKeepsDeclarationOrder() throws Exception {
    // record components are creator properties: written first, in declaration order
    String json3 = v3.writeValueAsString(new Recipe("pancakes", 4, LocalDate.of(2026, 10, 5)));
    assertThat(json3).startsWith("{\"name\":\"pancakes\",\"servings\":4");
  }

  @Test
  void enumsUseToString() throws Exception {
    String json2 = v2.writeValueAsString(new Dish("pancakes", Difficulty.EASY));
    String json3 = v3.writeValueAsString(new Dish("pancakes", Difficulty.EASY));
    print("WRITE_ENUMS_USING_TO_STRING", json2, json3);

    assertThat(json2).isEqualTo("{\"name\":\"pancakes\",\"difficulty\":\"EASY\"}");
    assertThat(json3).isEqualTo("{\"name\":\"pancakes\",\"difficulty\":\"easy\"}");
  }

  @Test
  void unknownPropertiesIgnored() {
    String json = "{\"name\":\"pancakes\",\"servings\":4,\"rating\":5}";

    assertThatThrownBy(() -> v2.readValue(json, Recipe.class))
        .isInstanceOf(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class)
        .hasMessageContaining("Unrecognized field \"rating\"");
    Recipe recipe = v3.readValue(json, Recipe.class);
    print("FAIL_ON_UNKNOWN_PROPERTIES", "UnrecognizedPropertyException", recipe.toString());

    assertThat(recipe.name()).isEqualTo("pancakes");
  }

  @Test
  void trailingTokensFail() throws Exception {
    String json = "{\"name\":\"pancakes\",\"servings\":4} {\"name\":\"waffles\"}";

    Recipe recipe = v2.readValue(json, Recipe.class);
    assertThat(recipe.name()).isEqualTo("pancakes");
    assertThatThrownBy(() -> v3.readValue(json, Recipe.class))
        .isInstanceOf(tools.jackson.databind.exc.MismatchedInputException.class)
        .hasMessageContaining("Trailing token");
    print("FAIL_ON_TRAILING_TOKENS", recipe.toString(), "MismatchedInputException");
  }

  @Test
  void nullForPrimitiveFails() throws Exception {
    String json = "{\"grams\":null}";

    Portion portion = v2.readValue(json, Portion.class);
    assertThat(portion.getGrams()).isZero();
    assertThatThrownBy(() -> v3.readValue(json, Portion.class))
        .isInstanceOf(tools.jackson.databind.exc.MismatchedInputException.class)
        .hasMessageContaining("Cannot map `null` into type `int`");
    print("FAIL_ON_NULL_FOR_PRIMITIVES", "grams=0", "MismatchedInputException");
  }

  @Test
  void emptyBeansWrittenAsEmptyObject() {
    assertThatThrownBy(() -> v2.writeValueAsString(new Empty()))
        .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class)
        .hasMessageContaining("No serializer found");
    String json3 = v3.writeValueAsString(new Empty());
    print("FAIL_ON_EMPTY_BEANS", "InvalidDefinitionException", json3);

    assertThat(json3).isEqualTo("{}");
  }

  @Test
  void viewExcludesPropertiesWithoutView() throws Exception {
    Review review = Review.of(5, "Lokesh");
    String json2 = v2.writerWithView(Review.Public.class).writeValueAsString(review);
    String json3 = v3.writerWithView(Review.Public.class).writeValueAsString(review);
    print("DEFAULT_VIEW_INCLUSION", json2, json3);

    assertThat(json2).isEqualTo("{\"stars\":5,\"author\":\"Lokesh\"}");
    assertThat(json3).isEqualTo("{\"stars\":5}");
  }

  @Test
  void jackson2DefaultsBuilderRestoresOldOutput() {
    tools.jackson.databind.json.JsonMapper legacy = tools.jackson.databind.json.JsonMapper.builderWithJackson2Defaults().build();

    String recipe = legacy.writeValueAsString(new Recipe("pancakes", 4, LocalDate.of(2026, 10, 5)));
    String ingredient = legacy.writeValueAsString(Ingredient.of("flour", 250, true));
    String dish = legacy.writeValueAsString(new Dish("pancakes", Difficulty.EASY));
    System.out.println("builderWithJackson2Defaults: " + recipe + " " + ingredient + " " + dish);

    assertThat(recipe).isEqualTo("{\"name\":\"pancakes\",\"servings\":4,\"createdOn\":[2026,10,5]}");
    assertThat(ingredient).isEqualTo("{\"name\":\"flour\",\"grams\":250,\"vegan\":true}");
    assertThat(dish).isEqualTo("{\"name\":\"pancakes\",\"difficulty\":\"EASY\"}");
  }

  @Test
  void singleFeatureCanBeTurnedBack() {
    tools.jackson.databind.json.JsonMapper strict = tools.jackson.databind.json.JsonMapper.builder()
        .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();

    assertThatThrownBy(() -> strict.readValue("{\"rating\":5}", Recipe.class))
        .isInstanceOf(tools.jackson.databind.exc.UnrecognizedPropertyException.class);
    String date = strict.writeValueAsString(LocalDate.of(2026, 10, 5));
    assertThat(date).isEqualTo("[2026,10,5]");
  }

  private static void print(String feature, String jackson2, String jackson3) {
    System.out.printf("%-30s | 2.x: %-55s | 3.x: %s%n", feature, jackson2, jackson3);
  }
}
