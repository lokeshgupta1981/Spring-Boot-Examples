package com.howtodoinjava.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/** Illustrates a narrower advice type; intentionally not a registered Spring bean. */
@Aspect
class BeforeAdviceExample {
  @Before("@annotation(com.howtodoinjava.aop.Measured)")
  public void countAttempt(JoinPoint joinPoint) {
    System.out.println("Attempted: " + joinPoint.getSignature().getName());
  }
}
