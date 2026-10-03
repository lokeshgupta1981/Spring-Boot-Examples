package com.howtodoinjava.springboothibernate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RepairJobControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  void listByStatus() throws Exception {
    mvc.perform(get("/api/jobs").param("status", "DONE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bikeModel").value("Trek"))
        .andExpect(jsonPath("$[0].mechanic").value("Anna"))
        .andExpect(jsonPath("$[1].bikeModel").value("Cube"));
  }

  @Test
  void createAndComplete() throws Exception {
    String body = mvc.perform(post("/api/jobs").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mechanicId": 2, "bikeModel": "Specialized", "problem": "brakes", "cost": 35.00}"""))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("RECEIVED"))
        .andExpect(jsonPath("$.mechanic").value("Ravi"))
        .andReturn().getResponse().getContentAsString();
    String id = body.replaceAll(".*\"id\":(\\d+).*", "$1");

    mvc.perform(patch("/api/jobs/" + id + "/complete"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DONE"));
  }

  @Test
  void unknownJobIs404() throws Exception {
    mvc.perform(get("/api/jobs/99")).andExpect(status().isNotFound());
  }
}
