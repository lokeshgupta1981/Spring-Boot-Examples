package com.howtodoinjava.boot4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(PantryController.class)
@Import(PantryService.class)
class PantryControllerTest {

  @Autowired
  MockMvcTester mvc;

  @MockitoBean
  PriceService priceService;

  @Test
  void versionOneIsTheDefault() {
    assertThat(mvc.get().uri("/pantry/apple"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {"name":"apple","quantity":5}""");
  }

  @Test
  void versionTwoAddsBestBeforeAsIsoDate() {
    assertThat(mvc.get().uri("/pantry/apple").header("API-Version", "2"))
        .hasStatusOk()
        .bodyJson().extractingPath("$.bestBefore").isEqualTo("2026-10-12");
  }

  @Test
  void unknownVersionIsRejected() {
    var result = mvc.get().uri("/pantry/apple").header("API-Version", "3").exchange();
    System.out.println("API-Version 3 -> " + result.getResponse().getStatus()
        + " " + result.getMvcResult().getResolvedException());
    assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void unknownItemIsNotFound() {
    assertThat(mvc.get().uri("/pantry/kiwi"))
        .hasStatus(HttpStatus.NOT_FOUND)
        .failure().hasMessage("No pantry item: kiwi");
  }
}
