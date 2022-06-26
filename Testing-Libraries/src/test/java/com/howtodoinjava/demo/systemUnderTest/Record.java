package com.howtodoinjava.demo.systemUnderTest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Record {
  public Record(String name) {
    this.name = name;
  }

  private long id;
  private String name;
}
