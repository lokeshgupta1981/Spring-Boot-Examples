package com.howtodoinjava.demo.easymock;

import com.howtodoinjava.demo.systemUnderTest.Record;
import com.howtodoinjava.demo.systemUnderTest.RecordDao;
import com.howtodoinjava.demo.systemUnderTest.RecordService;
import com.howtodoinjava.demo.systemUnderTest.SequenceGenerator;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;

@ExtendWith(EasyMockExtension.class)
public class EasyMockTestsWithEasyMockSupport extends EasyMockSupport {

  @Test
  public void testSaveRecord() {
    //Prepare mocks
    RecordDao mockDao = EasyMock.mock(RecordDao.class);
    SequenceGenerator mockGenerator = EasyMock.mock(SequenceGenerator.class);
    RecordService service = new RecordService(mockGenerator, mockDao);

    Record record = new Record();
    record.setName("Test Record");

    //Record expectations
    expect(mockGenerator.getNext()).andReturn(100L);
    expect(mockDao.saveRecord(EasyMock.anyObject(Record.class))).andReturn(record);

    //Replay
    replayAll();

    //Test and assertions
    Record savedRecord = service.saveRecord(record);

        /*Assertions.assertEquals("Test Record", savedRecord.getName());
        Assertions.assertEquals(100L,savedRecord.getId());*/

    //Verify
    verifyAll();
  }
}
