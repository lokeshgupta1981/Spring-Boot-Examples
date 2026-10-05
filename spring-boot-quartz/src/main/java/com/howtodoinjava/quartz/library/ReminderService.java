package com.howtodoinjava.quartz.library;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** "Sends" emails by logging them and storing them in the sent_message table. */
@Service
public class ReminderService {

  private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

  private final LibraryRepository repository;
  private final Clock clock;

  public ReminderService(LibraryRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  /** Reminds every member whose loan is due in the given number of days. Returns the number of reminders. */
  public int sendDueDateReminders(int daysBefore) {
    LocalDate dueDate = LocalDate.now(clock).plusDays(daysBefore);
    List<Loan> loans = repository.openLoansDueOn(dueDate);
    loans.forEach(this::remind);
    return loans.size();
  }

  public void sendReminder(long loanId) {
    remind(repository.findLoan(loanId));
  }

  public int sendFineNotices() {
    List<Fine> fines = repository.finesCalculatedOn(LocalDate.now(clock));
    for (Fine fine : fines) {
      String text = "Fine of " + fine.amount() + " for '" + fine.book() + "' (" + fine.daysLate() + " days late)";
      send(new SentMessage(fine.loanId(), "FINE", fine.member(), text));
    }
    return fines.size();
  }

  private void remind(Loan loan) {
    String text = "'" + loan.book() + "' is due on " + loan.dueDate();
    send(new SentMessage(loan.id(), "REMINDER", loan.member(), text));
  }

  private void send(SentMessage message) {
    repository.saveMessage(message);
    log.info("Email to {}: {}", message.recipient(), message.text());
  }
}
