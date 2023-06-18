package com.howtodoinjava.demo.web.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatus implements Serializable {

  private String taskId;
  private String taskName;
  private float percentageComplete;
  private Status status;

  public enum Status {
    SUBMITTED, STARTED, RUNNING, FINISHED, TERMINATED
  }
}



