package com.howtodoinjava.mockbean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShippingController.class)
class ShippingControllerSliceTest {
  @Autowired MockMvc mvc;
  @MockitoBean ShippingRateService rates;

  @Test
  void returnsStubbedQuote() throws Exception {
    when(rates.quoteInCents("A-17")).thenReturn(249);

    mvc.perform(get("/shipping/A-17"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value("A-17"))
        .andExpect(jsonPath("$.cents").value(249));
    verify(rates).quoteInCents("A-17");
  }
}
