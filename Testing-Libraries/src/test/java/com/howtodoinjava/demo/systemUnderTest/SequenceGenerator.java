package com.howtodoinjava.demo.systemUnderTest;

import lombok.extern.java.Log;

@Log
public class SequenceGenerator {
  private long value = 1;

  public long getNext() {
    log.info("Get Next Id in SequenceGenerator");
    return value++;
  }
}
