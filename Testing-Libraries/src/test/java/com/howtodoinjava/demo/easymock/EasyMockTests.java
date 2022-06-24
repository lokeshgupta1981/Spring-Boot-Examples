package com.howtodoinjava.demo.easymock;

import com.howtodoinjava.demo.easymock.systemUnderTest.Record;
import com.howtodoinjava.demo.easymock.systemUnderTest.RecordDao;
import com.howtodoinjava.demo.easymock.systemUnderTest.RecordService;
import com.howtodoinjava.demo.easymock.systemUnderTest.SequenceGenerator;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;

public class EasyMockTests {

  @Test
  public void testSaveRecord() {
    RecordDao mockDao = EasyMock.mock(RecordDao.class);
    SequenceGenerator mockGenerator = EasyMock.mock(SequenceGenerator.class);

    Record record = new Record();
    record.setName("Test Record");

    expect(mockGenerator.getNext()).andReturn(100L);
    expect(mockDao.saveRecord(EasyMock.anyObject(Record.class))).andReturn(record);

    replay(mockGenerator);
    replay(mockDao);

    RecordService service = new RecordService(mockGenerator, mockDao);
    Record savedRecord = service.saveRecord(record);

    Assertions.assertEquals("Test Record", savedRecord.getName());
    Assertions.assertEquals(savedRecord.getId(), 100L);

    verify(mockGenerator);
    verify(mockDao);
  }
}

