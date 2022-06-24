package com.howtodoinjava.demo.easymock;

import com.howtodoinjava.demo.easymock.systemUnderTest.Record;
import com.howtodoinjava.demo.easymock.systemUnderTest.RecordDao;
import com.howtodoinjava.demo.easymock.systemUnderTest.RecordService;
import com.howtodoinjava.demo.easymock.systemUnderTest.SequenceGenerator;
import org.easymock.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.easymock.EasyMock.*;

@ExtendWith(EasyMockExtension.class)
public class EasyMockTestsWithAnnotationsJUnit5 {
    @Mock
    RecordDao mockDao;

    @Mock
    SequenceGenerator mockGenerator;

    @TestSubject
    RecordService service = new RecordService(mockGenerator, mockDao);

    @Test
    public void testSaveRecord() {
        Record record = new Record();
        record.setName("Test Record");

        //Set expectations
        expect(mockGenerator.getNext()).andReturn(100L);
        expect(mockDao.saveRecord(EasyMock.anyObject(Record.class))).andReturn(record);

        //Replay
        replay(mockGenerator);
        replay(mockDao);

        //Test and assertions
        RecordService service = new RecordService(mockGenerator, mockDao);
        Record savedRecord = service.saveRecord(record);

        Assertions.assertEquals("Test Record", savedRecord.getName());
        Assertions.assertEquals(savedRecord.getId(), 100L);

        //Verify
        verify(mockGenerator);
        verify(mockDao);
    }
}
