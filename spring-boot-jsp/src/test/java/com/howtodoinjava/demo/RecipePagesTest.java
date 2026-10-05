package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecipePagesTest {

  @LocalServerPort
  int port;

  RestTestClient client() {
    return RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void listPageRendersRecipesFromJsp() {
    String html = client().get().uri("/recipes")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class).returnResult().getResponseBody();

    assertThat(html).contains("<title>Recipes</title>");
    assertThat(html).contains("<td>Pancakes</td>");
    assertThat(html).contains("<td>Tomato Soup</td>");
    assertThat(html).doesNotContain("c:forEach");
  }

  @Test
  void formSubmitAddsRecipeAndRedirectsToList() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("name", "Omelette");
    form.add("minutes", "10");

    client().post().uri("/recipes")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.FOUND)
        .expectHeader().valueMatches("Location", ".*/recipes");

    String html = client().get().uri("/recipes")
        .exchange()
        .expectBody(String.class).returnResult().getResponseBody();

    assertThat(html).contains("<td>Omelette</td>");
  }
}
