package com.howtodoinjava.mockbean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShippingController.class)
@MockitoBean(types = ShippingRateService.class)
class ShippingControllerTypeLevelTest {
  @Autowired MockMvc mvc;
  @Autowired ShippingRateService rates;

  @Test
  void typeLevelMockSuppliesTheController() throws Exception {
    when(rates.quoteInCents("A-17")).thenReturn(149);
    mvc.perform(get("/shipping/A-17"))
        .andExpect(jsonPath("$.cents").value(149));
  }
}
