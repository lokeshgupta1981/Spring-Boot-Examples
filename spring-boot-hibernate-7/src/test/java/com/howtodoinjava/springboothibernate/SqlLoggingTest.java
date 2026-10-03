package com.howtodoinjava.springboothibernate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class SqlLoggingTest {

  @Autowired
  RepairJobRepository jobs;

  @Test
  void logsSqlAndBindParameters(CapturedOutput output) {
    jobs.findByStatus(RepairStatus.DONE);
    assertThat(output).contains("org.hibernate.SQL")
        .contains("rj1_0.status=?")
        .contains("org.hibernate.orm.jdbc.bind")
        .contains("binding parameter (1:ENUM) <- [DONE]");
  }
}
