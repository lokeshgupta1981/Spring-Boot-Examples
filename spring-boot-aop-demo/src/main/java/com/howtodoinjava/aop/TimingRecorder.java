package com.howtodoinjava.aop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class TimingRecorder {
  private final CopyOnWriteArrayList<String> methodNames = new CopyOnWriteArrayList<>();

  public void record(String methodName) {
    methodNames.add(methodName);
  }

  public List<String> methodNames() {
    return List.copyOf(methodNames);
  }

  public void clear() {
    methodNames.clear();
  }
}
