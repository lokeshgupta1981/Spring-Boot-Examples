package com.howtodoinjava.quartz.listeners;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Logs misfires. Returning true from vetoJobExecution() would cancel a run. */
@Component
public class TriggerAuditListener implements TriggerListener {

  private static final Logger log = LoggerFactory.getLogger(TriggerAuditListener.class);

  private final List<String> misfired = new CopyOnWriteArrayList<>();

  @Override
  public String getName() {
    return "triggerAudit";
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    return false;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    misfired.add(trigger.getKey().toString());
    log.warn("Misfire: {} missed its fire time", trigger.getKey());
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
                              CompletedExecutionInstruction instruction) {
  }

  public List<String> misfired() {
    return List.copyOf(misfired);
  }

  public void clear() {
    misfired.clear();
  }
}
