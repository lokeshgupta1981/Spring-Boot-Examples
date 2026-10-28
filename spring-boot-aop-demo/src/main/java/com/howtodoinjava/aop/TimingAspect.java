package com.howtodoinjava.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimingAspect {
  private final TimingRecorder recorder;

  public TimingAspect(TimingRecorder recorder) {
    this.recorder = recorder;
  }

  @Around("@annotation(com.howtodoinjava.aop.Measured)")
  public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.nanoTime();
    try {
      return joinPoint.proceed();
    } finally {
      long elapsed = System.nanoTime() - start;
      recorder.record(joinPoint.getSignature().getName());
      System.out.printf("%s took %d ns%n", joinPoint.getSignature().getName(), elapsed);
    }
  }
}
