package com.howtodoinjava.testpost;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {
    private final AtomicLong nextId = new AtomicLong(100);
    private final Map<Long, Reservation> reservations = new ConcurrentHashMap<>();

    public record CreateReservation(String productCode, int quantity) {}
    public record Reservation(long id, String productCode, int quantity) {}
    public record ErrorResponse(String message) {}

    @PostMapping("/reservations")
    ResponseEntity<?> create(@RequestBody CreateReservation request) {
        if (request.productCode() == null || request.productCode().isBlank()
                || request.quantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Product code and positive quantity are required"));
        }
        long id = nextId.getAndIncrement();
        Reservation reservation = new Reservation(id, request.productCode(), request.quantity());
        reservations.put(id, reservation);
        return ResponseEntity.created(URI.create("/reservations/" + id)).body(reservation);
    }

    @GetMapping("/reservations/{id}")
    ResponseEntity<Reservation> get(@PathVariable long id) {
        Reservation reservation = reservations.get(id);
        return reservation == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(reservation);
    }
}
