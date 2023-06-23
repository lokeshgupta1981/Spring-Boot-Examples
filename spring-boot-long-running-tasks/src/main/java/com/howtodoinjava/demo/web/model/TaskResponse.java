package com.howtodoinjava.demo.web.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse implements Serializable {

  private String taskId;
  private String name;
}
