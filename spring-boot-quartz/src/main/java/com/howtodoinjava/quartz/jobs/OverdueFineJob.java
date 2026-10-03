package com.howtodoinjava.quartz.jobs;

import java.math.BigDecimal;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.library.Fine;
import com.howtodoinjava.quartz.library.FineService;

/** Every night: calculates fines for overdue loans and keeps a run counter in its JobDataMap. */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class OverdueFineJob extends QuartzJobBean {

  private final FineService fineService;

  public OverdueFineJob(FineService fineService) {
    this.fineService = fineService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    List<Fine> fines = fineService.calculateFines();
    BigDecimal total = fines.stream().map(Fine::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

    JobDataMap data = context.getJobDetail().getJobDataMap();
    data.put("runCount", data.getIntValue("runCount") + 1);   // saved after the run
    data.put("lastTotal", total.toPlainString());
    context.setResult(fines.size());
  }
}
