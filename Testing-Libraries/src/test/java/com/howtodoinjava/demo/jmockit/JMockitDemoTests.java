package com.howtodoinjava.demo.jmockit;

import com.howtodoinjava.demo.systemUnderTest.Record;
import com.howtodoinjava.demo.systemUnderTest.*;
import mockit.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JMockitDemoTests {

  @Injectable
  RecordDao mockDao;

  @Injectable
  SequenceGenerator mockGenerator;

  @Tested
  RecordService service;

  @Test
  public void testSaveRecord(@Mocked NotificationService notificationService) {

    Record record = new Record();
    record.setName("Test Record");

    new Expectations() {{
      mockGenerator.getNext();
      result = 100L;
      times = 1;
    }};

    new Expectations() {{
      mockDao.saveRecord(record);
      result = record;
      times = 1;
    }};

    new Expectations() {{
      notificationService.sendNotification(anyString);
      result = true;
      times = 1;
    }};

    Record savedRecord = service.saveRecord(record);

    //Verify
    new Verifications() {{ // a "verification block"
      mockGenerator.getNext();
      times = 1;
    }};

    new Verifications() {{
      mockDao.saveRecord(record);
      times = 1;
    }};

    new Verifications() {{
      notificationService.sendNotification(anyString);
      times = 1;
    }};

    assertEquals("Test Record", savedRecord.getName());
    assertEquals(100L, savedRecord.getId());
  }
}

