package com.howtodoinjava.demo.easymock;

import com.howtodoinjava.demo.systemUnderTest.Record;
import com.howtodoinjava.demo.systemUnderTest.RecordDao;
import com.howtodoinjava.demo.systemUnderTest.RecordService;
import com.howtodoinjava.demo.systemUnderTest.SequenceGenerator;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class EasyMockTests {

  @Test
  public void testSaveRecord() {
    //Prepare mocks
    RecordDao mockDao = EasyMock.mock(RecordDao.class);
    SequenceGenerator mockGenerator = EasyMock.mock(SequenceGenerator.class);

    Record record = new Record();
    record.setName("Test Record");

    //Set expectations
    //expect(mockGenerator.getNext()).andReturn(100L).once();
    mockGenerator.getNext();
    expectLastCall().andReturn((long) 100);
    expect(mockDao.saveRecord(EasyMock.anyObject(Record.class))).andReturn(record).once();

    //Replay
    replay(mockGenerator);
    replay(mockDao);

    //Test and assertions
    RecordService service = new RecordService(mockGenerator, mockDao);
    Record savedRecord = service.saveRecord(record);

    assertEquals("Test Record", savedRecord.getName());
    assertEquals(100L, savedRecord.getId());

    //Verify
    verify(mockGenerator);
    verify(mockDao);
  }
}

