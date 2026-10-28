package com.howtodoinjava.testpost;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ReservationControllerTest {
    @Autowired TestRestTemplate client;

    @Test void createsReservationAndCanReadItBack() {
        var request = new ReservationController.CreateReservation("LAPTOP-15", 2);
        ResponseEntity<ReservationController.Reservation> created = client.postForEntity(
                "/reservations", request, ReservationController.Reservation.class);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        assertEquals("LAPTOP-15", created.getBody().productCode());
        assertEquals(2, created.getBody().quantity());
        assertNotNull(created.getHeaders().getLocation());
        assertEquals("/reservations/" + created.getBody().id(),
                created.getHeaders().getLocation().toString());

        ResponseEntity<ReservationController.Reservation> fetched = client.getForEntity(
                created.getHeaders().getLocation(), ReservationController.Reservation.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(created.getBody(), fetched.getBody());
    }

    @Test void invalidRequestReturns400InsteadOfThrowing() {
        var request = new ReservationController.CreateReservation("", 0);
        ResponseEntity<ReservationController.ErrorResponse> response = client.postForEntity(
                "/reservations", request, ReservationController.ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product code and positive quantity are required", response.getBody().message());
    }

    @Test void postForObjectReturnsBodyOnly() {
        var request = new ReservationController.CreateReservation("MOUSE-7", 1);
        ReservationController.Reservation reservation = client.postForObject(
                "/reservations", request, ReservationController.Reservation.class);
        assertNotNull(reservation);
        assertEquals("MOUSE-7", reservation.productCode());
    }
}
