package com.howtodoinjava.asyncmvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.*;
@WebMvcTest(ReportController.class)
@Import(ReportService.class)
class ReportControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void completableFutureReturnsReport() throws Exception {
    MvcResult mvcResult = mockMvc.perform(get("/reports/2026-09"))
        .andExpect(request().asyncStarted())
        .andExpect(request().asyncResult(new Report("2026-09", 95)))
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json("{\"month\":\"2026-09\",\"orders\":95}"));
  }
@Test
void callableReturnsText() throws Exception {
  MvcResult mvcResult = mockMvc.perform(get("/reports/status"))
      .andExpect(request().asyncStarted())
      .andReturn();

  mockMvc.perform(asyncDispatch(mvcResult))
      .andExpect(status().isOk())
      .andExpect(content().string("Reports are up to date"));
}

@Test
void deferredResultReturnsReport() throws Exception {
  MvcResult mvcResult = mockMvc.perform(get("/reports/2026-08/deferred"))
      .andExpect(request().asyncStarted())
      .andReturn();

  mockMvc.perform(asyncDispatch(mvcResult))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orders").value(120));
}
@Test
void exceptionInAsyncTaskGoesToExceptionHandler() throws Exception {
  MvcResult mvcResult = mockMvc.perform(get("/reports/2026-01"))
      .andExpect(request().asyncStarted())
      .andReturn();

  mockMvc.perform(asyncDispatch(mvcResult))
      .andExpect(status().isNotFound())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.detail").value("No report for 2026-01"));
}
}
