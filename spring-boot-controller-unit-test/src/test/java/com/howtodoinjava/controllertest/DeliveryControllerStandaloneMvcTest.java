package com.howtodoinjava.controllertest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DeliveryControllerStandaloneMvcTest {
  DeliveryService service;
  MockMvc mvc;

  @BeforeEach
  void setUp() {
    service = mock(DeliveryService.class);
    mvc = MockMvcBuilders.standaloneSetup(new DeliveryController(service)).build();
  }

  @Test
  void getSerializesTheStatusAsJson() throws Exception {
    when(service.find("D-17"))
        .thenReturn(Optional.of(new DeliveryStatus("D-17", "OUT_FOR_DELIVERY")));
    mvc.perform(get("/deliveries/D-17"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.trackingId").value("D-17"))
        .andExpect(jsonPath("$.state").value("OUT_FOR_DELIVERY"));
  }

  @Test
  void postReturnsLocationAndBody() throws Exception {
    when(service.create("12 Main Street"))
        .thenReturn(new DeliveryStatus("D-18", "CREATED"));
    mvc.perform(post("/deliveries").contentType(MediaType.APPLICATION_JSON)
            .content("{\"address\":\"12 Main Street\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/deliveries/D-18"))
        .andExpect(jsonPath("$.trackingId").value("D-18"));
  }
}
