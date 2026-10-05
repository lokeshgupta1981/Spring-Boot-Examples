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

@SpringBootTest(properties = {
    "springdoc.api-docs.path=/api-docs",
    "springdoc.swagger-ui.path=/docs",
    "springdoc.override-with-generic-response=false",
    "springdoc.packages-to-scan=com.howtodoinjava.demo.web",
    "springdoc.paths-to-match=/recipes/**, /admin/**"
})
@AutoConfigureMockMvc
class CustomPropertiesTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void apiDocsMoveToTheConfiguredPath() {
    assertThat(mvc.get().uri("/api-docs").exchange()).hasStatusOk();
    assertThat(mvc.get().uri("/v3/api-docs").exchange()).hasStatus(404);
  }

  @Test
  void swaggerUiMovesToTheConfiguredPath() {
    assertThat(mvc.get().uri("/docs").exchange()).hasStatus(302).hasRedirectedUrl("/swagger-ui/index.html");
  }

  @Test
  void onlyMatchingPackagesAndPathsAreDocumented() {
    MvcTestResult result = mvc.get().uri("/api-docs").exchange();
    JsonNode docs = JsonMapper.builder().build().readTree(result.getResponse().getContentAsByteArray());

    assertThat(docs.get("paths").propertyNames()).containsExactly("/recipes", "/recipes/{id}", "/admin/stats");
  }

  @Test
  void controllerAdviceStatusIsNoLongerAddedEverywhere() {
    MvcTestResult result = mvc.get().uri("/api-docs").exchange();
    JsonNode docs = JsonMapper.builder().build().readTree(result.getResponse().getContentAsByteArray());

    // GET /recipes has no 404 any more, GET /recipes/{id} keeps the one from @ApiResponse
    assertThat(docs.at("/paths/~1recipes/get/responses/404").isMissingNode()).isTrue();
    assertThat(docs.at("/paths/~1recipes~1{id}/get/responses/404").isMissingNode()).isFalse();
  }
}
