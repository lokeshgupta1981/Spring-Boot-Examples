package com.howtodoinjava.quartz;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.howtodoinjava.quartz.listeners.JobAuditListener;

@SpringBootTest
@AutoConfigureMockMvc
class QuartzActuatorTest {

  @Autowired MockMvc mvc;
  @Autowired JobAuditListener jobAudit;

  @Test
  void listsGroupsJobsAndTriggers() throws Exception {
    mvc.perform(get("/actuator/quartz"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jobs.groups", contains("library")))
        .andExpect(jsonPath("$.triggers.groups", contains("library")));

    mvc.perform(get("/actuator/quartz/jobs"))
        .andExpect(jsonPath("$.groups.library.jobs", contains("dueDateReminderJob", "fineNoticeJob", "overdueFineJob")));

    mvc.perform(get("/actuator/quartz/triggers/library/fineTrigger"))
        .andDo(print())
        .andExpect(jsonPath("$.type").value("cron"))
        .andExpect(jsonPath("$.cron.expression").value("0 0 2 * * ?"))
        .andExpect(jsonPath("$.state").value("NORMAL"));
  }

  @Test
  void jobDataValuesAreMaskedByDefault() throws Exception {
    mvc.perform(get("/actuator/quartz/jobs/library/overdueFineJob"))
        .andDo(print())
        .andExpect(jsonPath("$.className").value("com.howtodoinjava.quartz.jobs.OverdueFineJob"))
        .andExpect(jsonPath("$.durable").value(true))
        .andExpect(jsonPath("$.data.runCount").value("******"))
        .andExpect(jsonPath("$.triggers", hasSize(1)));
  }

  @Test
  void postTriggersTheJobNow() throws Exception {
    int before = jobAudit.runsOf("library.dueDateReminderJob").size();
    mvc.perform(post("/actuator/quartz/jobs/library/dueDateReminderJob")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"state\":\"running\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("dueDateReminderJob"));

    await().atMost(Duration.ofSeconds(5))
        .until(() -> jobAudit.runsOf("library.dueDateReminderJob").size() == before + 1);
  }
}
