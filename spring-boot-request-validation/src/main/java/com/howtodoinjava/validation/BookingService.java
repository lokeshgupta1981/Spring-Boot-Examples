package com.howtodoinjava.validation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * In-memory booking store.
 */
@Service
public class BookingService {

  private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
  private final AtomicLong ids = new AtomicLong();

  public Booking create(BookingRequest request) {
    long id = ids.incrementAndGet();
    Booking booking = new Booking(id, request.name(), request.email(), request.seats());
    bookings.put(id, booking);
    return booking;
  }

  public Booking update(Long id, BookingRequest request) {
    Booking booking = new Booking(id, request.name(), request.email(), request.seats());
    bookings.put(id, booking);
    return booking;
  }

  public Booking find(Long id) {
    Booking booking = bookings.get(id);
    if (booking == null) {
      throw new BookingNotFoundException(id);
    }
    return booking;
  }

  public List<Booking> search(String name, int limit) {
    return bookings.values().stream()
        .filter(b -> b.name().equalsIgnoreCase(name))
        .limit(limit)
        .toList();
  }
}
