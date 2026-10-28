package com.howtodoinjava.resttemplate;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AppointmentController {
    public record CreateAppointment(String title) {}
    public record Appointment(long id, String title) {}

    private final AtomicLong nextId = new AtomicLong(101);
    private final ConcurrentHashMap<Long, Appointment> appointments = new ConcurrentHashMap<>();

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> create(@RequestBody CreateAppointment request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        long id = nextId.getAndIncrement();
        Appointment saved = new Appointment(id, request.title());
        appointments.put(id, saved);
        return ResponseEntity.created(URI.create("/appointments/" + id)).body(saved);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> find(@PathVariable long id,
                    @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        Appointment item = required(id);
        var response = ResponseEntity.ok();
        if (requestId != null) {
            response.header("X-Request-Id", requestId);
        }
        return response.body(item);
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<Void> replace(@PathVariable long id, @RequestBody CreateAppointment request) {
        required(id);
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        appointments.put(id, new Appointment(id, request.title()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> remove(@PathVariable long id) {
        if (appointments.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        return ResponseEntity.noContent().build();
    }

    private Appointment required(long id) {
        Appointment item = appointments.get(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        return item;
    }
}
