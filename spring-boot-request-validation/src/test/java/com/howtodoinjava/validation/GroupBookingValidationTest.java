package com.howtodoinjava.validation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Nested objects, lists, the custom @OpenDay constraint and validation groups.
 */
@WebMvcTest({GroupBookingController.class, YogaClassController.class})
class GroupBookingValidationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void validGroupBooking() throws Exception {
    mockMvc.perform(post("/group-bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"groupName": "Morning crew", "date": "2026-10-06",
                 "contact": {"email": "amit@example.com", "phone": "9876543210"},
                 "attendees": [{"name": "Amit", "age": 35}, {"name": "Riya", "age": 14}]}
                """))
        .andExpect(status().isCreated());
  }

  @Test
  void nestedListAndCustomConstraintErrors() throws Exception {
    mockMvc.perform(post("/group-bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"groupName": "Morning crew", "date": "2026-10-05",
                 "contact": {"email": "amit-at-example", "phone": "12345"},
                 "attendees": [{"name": "Amit", "age": 35}, {"name": "", "age": 9}]}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", aMapWithSize(5)))
        .andExpect(jsonPath("$.errors['date']").value("studio is closed on Mondays"))
        .andExpect(jsonPath("$.errors['contact.email']").value("must be a well-formed email address"))
        .andExpect(jsonPath("$.errors['contact.phone']").value("must be 10 digits"))
        .andExpect(jsonPath("$.errors['attendees[1].name']").value("must not be blank"))
        .andExpect(jsonPath("$.errors['attendees[1].age']").value("must be greater than or equal to 12"));
  }

  @Test
  void emptyAttendeeList() throws Exception {
    mockMvc.perform(post("/group-bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"groupName": "Morning crew", "date": "2026-10-06",
                 "contact": {"email": "amit@example.com"}, "attendees": []}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.attendees").value("must not be empty"));
  }

  @Test
  void createGroupRejectsId() throws Exception {
    mockMvc.perform(post("/classes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id": 3, "title": "", "capacity": 12}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").value("must be null"))
        .andExpect(jsonPath("$.errors.title").value("must not be blank"));

    mockMvc.perform(post("/classes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "Hatha basics", "capacity": 12}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(7));
  }

  @Test
  void updateGroupRequiresId() throws Exception {
    mockMvc.perform(put("/classes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "Hatha basics", "capacity": 40}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").value("must not be null"))
        .andExpect(jsonPath("$.errors.capacity").value("must be less than or equal to 30"));
  }
}
