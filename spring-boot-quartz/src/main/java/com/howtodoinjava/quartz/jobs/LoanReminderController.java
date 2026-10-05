package com.howtodoinjava.quartz.jobs;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanReminderController {

  private final LoanReminderScheduler loanReminderScheduler;

  public LoanReminderController(LoanReminderScheduler loanReminderScheduler) {
    this.loanReminderScheduler = loanReminderScheduler;
  }

  @PostMapping("/loans/{id}/reminder")
  public Map<String, Object> remind(@PathVariable long id, @RequestParam(defaultValue = "10") long inSeconds)
      throws SchedulerException {
    Date firstFire = loanReminderScheduler.scheduleReminder(id, Instant.now().plusSeconds(inSeconds));
    return Map.of("loanId", id, "sendAt", firstFire.toInstant().toString());
  }
}
