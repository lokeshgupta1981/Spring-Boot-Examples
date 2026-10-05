package com.howtodoinjava.validation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Asserts the validation responses shown in the article.
 */
@WebMvcTest(BookingController.class)
@Import(BookingService.class)
class BookingValidationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void validBookingIsCreated() throws Exception {
    mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Lokesh", "email": "lokesh@example.com", "seats": 2}
                """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/bookings/1"))
        .andExpect(jsonPath("$.seats").value(2));
  }

  @Test
  void invalidBodyReturnsFieldErrors() throws Exception {
    mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "", "email": "lokesh-at-example", "seats": 9}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.detail").value("Validation failed"))
        .andExpect(jsonPath("$.errors", aMapWithSize(3)))
        .andExpect(jsonPath("$.errors.name").value("must not be blank"))
        .andExpect(jsonPath("$.errors.email").value("must be a well-formed email address"))
        .andExpect(jsonPath("$.errors.seats").value("must be less than or equal to 4"));
  }

  @Test
  void invalidPathVariable() throws Exception {
    mockMvc.perform(get("/bookings/{id}", 0))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").value("must be greater than or equal to 1"));
  }

  @Test
  void invalidRequestParams() throws Exception {
    mockMvc.perform(get("/bookings").param("name", "Lokesh").param("limit", "100"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.limit").value("must be less than or equal to 50"));

    mockMvc.perform(get("/bookings").param("name", " ").param("limit", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").value("must not be blank"))
        .andExpect(jsonPath("$.errors.limit").value("must be greater than or equal to 1"));
  }

  @Test
  void missingRequestParam() throws Exception {
    mockMvc.perform(get("/bookings").param("limit", "5"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Required parameter 'name' is not present."));
  }

  @Test
  void pathVariableAndBodyErrorsTogether() throws Exception {
    mockMvc.perform(put("/bookings/{id}", 0)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "", "email": "lokesh@example.com", "seats": 2}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").value("must be greater than or equal to 1"))
        .andExpect(jsonPath("$.errors.name").value("must not be blank"));
  }

  @Test
  void unknownBookingReturns404() throws Exception {
    mockMvc.perform(get("/bookings/{id}", 5))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Booking 5 not found"));
  }

  @Test
  void listBodyErrorsCarryTheIndex() throws Exception {
    mockMvc.perform(post("/bookings/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"name": "Lokesh", "email": "lokesh@example.com", "seats": 2},
                 {"name": "", "email": "amit@example.com", "seats": 9}]
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", aMapWithSize(2)))
        .andExpect(jsonPath("$.errors['requests[1].name']").value("must not be blank"))
        .andExpect(jsonPath("$.errors['requests[1].seats']").value("must be less than or equal to 4"));
  }

  @Test
  void wrongJsonTypeFailsBeforeValidation() throws Exception {
    mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Lokesh", "email": "lokesh@example.com", "seats": "two"}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Failed to read request"))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }
}
