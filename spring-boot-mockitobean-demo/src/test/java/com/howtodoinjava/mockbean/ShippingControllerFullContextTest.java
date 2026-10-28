package com.howtodoinjava.mockbean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShippingControllerFullContextTest {
  @Autowired MockMvc mvc;
  @MockitoBean(name = "shippingRateService", enforceOverride = true) ShippingRateService rates;

  @Test
  void replacesExistingService() throws Exception {
    when(rates.quoteInCents("A-17")).thenReturn(199);
    mvc.perform(get("/shipping/A-17"))
        .andExpect(jsonPath("$.cents").value(199));
  }
}
