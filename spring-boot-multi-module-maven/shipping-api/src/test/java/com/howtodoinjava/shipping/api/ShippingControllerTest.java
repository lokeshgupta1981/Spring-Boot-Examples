package com.howtodoinjava.shipping.api;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
@SpringBootTest @AutoConfigureMockMvc
class ShippingControllerTest {
  @Autowired MockMvc mvc;
  @Test void returnsQuoteFromLibrary() throws Exception {
    mvc.perform(get("/shipping/quote").param("weightKg", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.weightKg").value(3))
        .andExpect(jsonPath("$.fee").value(76));
  }
}

