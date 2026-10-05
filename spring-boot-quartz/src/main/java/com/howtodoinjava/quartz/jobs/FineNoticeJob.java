package com.howtodoinjava.quartz.jobs;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.library.ReminderService;

/** Has no trigger: runs after OverdueFineJob through the JobChainingJobListener. */
public class FineNoticeJob extends QuartzJobBean {

  private final ReminderService reminderService;

  public FineNoticeJob(ReminderService reminderService) {
    this.reminderService = reminderService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    context.setResult(reminderService.sendFineNotices());
  }
}
