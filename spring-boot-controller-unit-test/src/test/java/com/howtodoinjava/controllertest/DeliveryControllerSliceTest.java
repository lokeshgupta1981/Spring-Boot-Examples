package com.howtodoinjava.controllertest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerSliceTest {
  @Autowired MockMvc mvc;
  @MockitoBean DeliveryService service;

  @Test
  void testsBootConfiguredMvcResponse() throws Exception {
    when(service.find("D-17"))
        .thenReturn(Optional.of(new DeliveryStatus("D-17", "OUT_FOR_DELIVERY")));
    mvc.perform(get("/deliveries/D-17"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.state").value("OUT_FOR_DELIVERY"));
  }

  @Test
  void returns404WhenServiceReturnsEmpty() throws Exception {
    when(service.find("missing")).thenReturn(Optional.empty());
    mvc.perform(get("/deliveries/missing"))
        .andExpect(status().isNotFound());
  }
}
