package com.howtodoinjava.quartz;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "management.endpoint.quartz.show-values=always")
@AutoConfigureMockMvc
class ShowValuesActuatorTest {

  @Autowired MockMvc mvc;

  @Test
  void showValuesAlwaysPrintsTheJobData() throws Exception {
    mvc.perform(get("/actuator/quartz/jobs/library/dueDateReminderJob"))
        .andExpect(jsonPath("$.data.daysBefore").value(1));
  }
}
