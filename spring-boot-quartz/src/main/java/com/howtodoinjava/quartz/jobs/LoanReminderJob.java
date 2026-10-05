package com.howtodoinjava.quartz.jobs;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.library.ReminderService;

/** A one-time reminder for one loan, scheduled from code with a SimpleTrigger. */
public class LoanReminderJob extends QuartzJobBean {

  private final ReminderService reminderService;
  private long loanId;

  public LoanReminderJob(ReminderService reminderService) {
    this.reminderService = reminderService;
  }

  public void setLoanId(long loanId) {
    this.loanId = loanId;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    reminderService.sendReminder(loanId);
  }
}
