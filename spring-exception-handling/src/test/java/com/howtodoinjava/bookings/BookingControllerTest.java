package com.howtodoinjava.bookings;

import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest({BookingController.class, GuestController.class})
class BookingControllerTest {

  @Autowired
  MockMvcTester mvc;

  @MockitoBean
  BookingService service;

  @Test
  void localHandlerWinsOverGlobalHandler() {
    given(service.find(5L)).willThrow(new BookingNotFoundException(5));

    mvc.get().uri("/bookings/5")
        .assertThat()
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
        .bodyJson()
        .hasPathSatisfying("$.title", title -> title.assertThat().isEqualTo("Booking not found"))
        .hasPathSatisfying("$.detail", d -> d.assertThat().isEqualTo("Booking 5 does not exist"))
        .hasPathSatisfying("$.instance", i -> i.assertThat().isEqualTo("/bookings/5"));
  }

  @Test
  void globalHandlerServesControllersWithoutLocalHandler() {
    given(service.find(5L)).willThrow(new BookingNotFoundException(5));

    mvc.get().uri("/guests/5/name")
        .assertThat()
        .hasStatus(HttpStatus.NOT_FOUND)
        .bodyJson()
        .hasPathSatisfying("$.title", title -> title.assertThat().isEqualTo("Not found (global handler)"));
  }

  @Test
  void globalHandlerReturnsProblemDetailWithExtraProperty() {
    given(service.create(new BookingRequest("Amit", 3))).willThrow(new RoomUnavailableException("Amit"));

    mvc.post().uri("/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"guest\":\"Amit\",\"nights\":3}")
        .assertThat()
        .hasStatus(HttpStatus.CONFLICT)
        .bodyJson()
        .hasPathSatisfying("$.status", s -> s.assertThat().isEqualTo(409))
        .hasPathSatisfying("$.detail", d -> d.assertThat().isEqualTo("No room is free for Amit"))
        .hasPathSatisfying("$.supportEmail", e -> e.assertThat().isEqualTo("desk@example.com"));
  }

  @Test
  void validationErrorsAreListedPerField() {
    mvc.post().uri("/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"guest\":\"\",\"nights\":0}")
        .assertThat()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.title", t -> t.assertThat().isEqualTo("Validation failed"))
        .hasPathSatisfying("$.errors.guest", g -> g.assertThat().isEqualTo("guest name is required"))
        .hasPathSatisfying("$.errors.nights", n -> n.assertThat().isEqualTo("nights must be at least 1"));
  }

  @Test
  void responseStatusOnExceptionClassSetsTheStatus() {
    given(service.find(99L)).willThrow(new BookingCancelledException(99));

    mvc.get().uri("/bookings/99")
        .assertThat()
        .hasStatus(HttpStatus.GONE);
  }

  @Test
  void catchAllHandlerHidesInternalErrors() {
    given(service.rebook(1L)).willThrow(new UnsupportedOperationException("Rebooking is not supported yet"));

    mvc.post().uri("/bookings/1/rebook")
        .assertThat()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .bodyJson()
        .hasPathSatisfying("$.title", t -> t.assertThat().isEqualTo("Unexpected error"))
        .hasPathSatisfying("$.detail", d -> d.assertThat().isEqualTo("Something went wrong, please retry later"));
  }

  @Test
  void oneHandlerForSeveralExceptionTypes() {
    given(service.find(0L)).willThrow(new IllegalArgumentException("id must be positive, was 0"));

    mvc.get().uri("/bookings/0")
        .assertThat()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.detail", d -> d.assertThat().isEqualTo("id must be positive, was 0"));
  }

  @Test
  void springMvcExceptionsGetProblemDetailToo() {
    mvc.delete().uri("/bookings/1")
        .assertThat()
        .hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
        .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
        .bodyJson()
        .hasPathSatisfying("$.title", t -> t.assertThat().isEqualTo("Method Not Allowed"));
  }
}
