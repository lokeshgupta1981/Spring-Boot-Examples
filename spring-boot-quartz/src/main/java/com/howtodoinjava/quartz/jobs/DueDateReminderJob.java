package com.howtodoinjava.quartz.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.library.ReminderService;

/** Every morning: emails members whose books are due in "daysBefore" days. */
@DisallowConcurrentExecution
public class DueDateReminderJob extends QuartzJobBean {

  private final ReminderService reminderService;   // Spring bean, constructor injection
  private int daysBefore = 1;                         // set from the JobDataMap

  public DueDateReminderJob(ReminderService reminderService) {
    this.reminderService = reminderService;
  }

  public void setDaysBefore(int daysBefore) {
    this.daysBefore = daysBefore;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    int sent = reminderService.sendDueDateReminders(daysBefore);
    context.setResult(sent);
  }
}
