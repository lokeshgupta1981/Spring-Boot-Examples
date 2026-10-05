package com.howtodoinjava.quartz.library;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

/** Charges 0.25 per day for every loan that is past its due date and not returned. */
@Service
public class FineService {

  static final BigDecimal FINE_PER_DAY = new BigDecimal("0.25");

  private final LibraryRepository repository;
  private final Clock clock;

  public FineService(LibraryRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  public List<Fine> calculateFines() {
    LocalDate today = LocalDate.now(clock);
    List<Fine> fines = repository.overdueLoans(today).stream()
        .map(loan -> {
          long daysLate = ChronoUnit.DAYS.between(loan.dueDate(), today);
          return new Fine(loan.id(), loan.member(), loan.book(), daysLate,
              FINE_PER_DAY.multiply(BigDecimal.valueOf(daysLate)));
        })
        .toList();
    fines.forEach(fine -> repository.saveFine(fine, today));
    return fines;
  }
}
