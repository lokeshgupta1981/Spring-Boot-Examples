package com.howtodoinjava.bookings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
  private final AtomicLong nextId = new AtomicLong(1);

  public BookingService() {
    create(new BookingRequest("Lokesh", 2));
  }

  public Booking find(long id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be positive, was " + id);
    }
    if (id == 99) {
      throw new BookingCancelledException(id);
    }
    Booking booking = bookings.get(id);
    if (booking == null) {
      throw new BookingNotFoundException(id);
    }
    return booking;
  }

  public Booking create(BookingRequest request) {
    if (bookings.size() >= 3) {
      throw new RoomUnavailableException(request.guest());
    }
    Booking booking = new Booking(nextId.getAndIncrement(), request.guest(), request.nights());
    bookings.put(booking.id(), booking);
    return booking;
  }

  public Booking rebook(long id) {
    find(id);
    throw new UnsupportedOperationException("Rebooking is not supported yet");
  }
}
