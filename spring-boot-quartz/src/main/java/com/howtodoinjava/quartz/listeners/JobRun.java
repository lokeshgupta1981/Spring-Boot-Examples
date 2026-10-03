package com.howtodoinjava.quartz.listeners;

import java.util.Date;

/** One finished job execution, as seen by the JobAuditListener. */
public record JobRun(String job, String trigger, Date scheduledFireTime, Date fireTime, long runTimeMs,
                     Object result, String error) {
}
