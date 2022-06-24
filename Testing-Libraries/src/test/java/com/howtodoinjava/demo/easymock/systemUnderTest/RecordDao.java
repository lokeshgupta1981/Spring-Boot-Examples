package com.howtodoinjava.demo.easymock.systemUnderTest;

import lombok.extern.java.Log;

@Log
public class RecordDao {

  public Record saveRecord(Record record) {
    log.info("Saving Record in RecordDao");
    return record;
  }
}
