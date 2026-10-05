package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ApiDocsTest {

  @Autowired
  MockMvcTester mvc;

  private final JsonMapper mapper = JsonMapper.builder().build();

  private JsonNode apiDocs(String path) {
    MvcTestResult result = mvc.get().uri(path).exchange();
    assertThat(result).hasStatusOk();
    return mapper.readTree(result.getResponse().getContentAsByteArray());
  }

  @Test
  void apiDocsDescribesTheRecipeEndpoints() {
    MvcTestResult result = mvc.get().uri("/v3/api-docs").exchange();
    assertThat(result).hasStatusOk();

    JsonNode docs = mapper.readTree(result.getResponse().getContentAsByteArray());
    assertThat(docs.get("openapi").asString()).startsWith("3.1");
    assertThat(docs.at("/info/title").asString()).isEqualTo("Recipe API");
    assertThat(docs.at("/paths/~1recipes/get/summary").asString()).isEqualTo("List all recipes");
    assertThat(docs.at("/paths/~1recipes~1{id}/get/responses/404/description").asString())
        .isEqualTo("No recipe with this id");
    assertThat(docs.at("/paths/~1recipes/post/responses/201").isMissingNode()).isFalse();
  }

  @Test
  void controllerAdviceStatusIsAddedToEveryOperation() {
    JsonNode docs = apiDocs("/v3/api-docs");

    // GlobalExceptionHandler maps RecipeNotFoundException to 404, so every operation lists 404
    assertThat(docs.at("/paths/~1recipes/get/responses/404").isMissingNode()).isFalse();
    assertThat(docs.at("/paths/~1admin~1stats/get/responses/404").isMissingNode()).isFalse();
  }

  @Test
  void validationAnnotationsAppearInTheSchema() {
    JsonNode docs = apiDocs("/v3/api-docs");
    JsonNode recipe = docs.at("/components/schemas/Recipe");

    assertThat(recipe.get("required")).extracting(JsonNode::asString).containsExactly("name");
    assertThat(recipe.at("/properties/prepMinutes/minimum").asInt()).isEqualTo(1);
    assertThat(recipe.at("/properties/id/readOnly").asBoolean()).isTrue();
  }

  @Test
  void hiddenEndpointIsNotDocumented() {
    JsonNode docs = apiDocs("/v3/api-docs");

    assertThat(docs.at("/paths/~1admin~1ping").isMissingNode()).isTrue();
    assertThat(docs.at("/paths/~1admin~1stats").isMissingNode()).isFalse();
  }

  @Test
  void securitySchemeIsDeclared() {
    JsonNode docs = apiDocs("/v3/api-docs");

    assertThat(docs.at("/components/securitySchemes/bearerAuth/scheme").asString()).isEqualTo("bearer");
    assertThat(docs.get("security").get(0).has("bearerAuth")).isTrue();
  }

  @Test
  void eachGroupHasItsOwnDocument() {
    JsonNode recipes = apiDocs("/v3/api-docs/recipes");
    JsonNode admin = apiDocs("/v3/api-docs/admin");

    assertThat(recipes.get("paths").propertyNames()).containsExactly("/recipes", "/recipes/{id}");
    assertThat(admin.get("paths").propertyNames()).containsExactly("/admin/stats");
  }

  @Test
  void yamlIsServedToo() {
    MvcTestResult result = mvc.get().uri("/v3/api-docs.yaml").exchange();

    assertThat(result).hasStatusOk().bodyText().startsWith("openapi: 3.1");
  }

  @Test
  void swaggerUiRedirectsToIndexHtml() {
    MvcTestResult result = mvc.get().uri("/swagger-ui.html").exchange();

    assertThat(result).hasStatus(302).hasRedirectedUrl("/swagger-ui/index.html");
  }
}
