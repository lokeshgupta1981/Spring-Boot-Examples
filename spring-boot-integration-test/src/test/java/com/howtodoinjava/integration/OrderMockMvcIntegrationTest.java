package com.howtodoinjava.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/orders.sql")
class OrderMockMvcIntegrationTest {
  @Autowired MockMvc mvc;

  @Test
  void readsDatabaseWithoutStartingServer() throws Exception {
    mvc.perform(get("/orders/17"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PACKED"));
  }
}
