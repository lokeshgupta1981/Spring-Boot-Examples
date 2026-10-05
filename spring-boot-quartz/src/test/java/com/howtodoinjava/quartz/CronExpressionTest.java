package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

/** Next fire times after Saturday 2026-10-03 10:15:00. */
class CronExpressionTest {

  static final ZoneId ZONE = ZoneId.of("UTC");
  static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 3, 10, 15, 0);

  @ParameterizedTest
  @CsvSource(delimiter = '|', textBlock = """
      0 0 2 * * ?            | 2026-10-04T02:00
      0 0 8 ? * MON-FRI      | 2026-10-05T08:00
      0 0/15 9-17 * * ?      | 2026-10-03T10:30
      0/30 * * * * ?         | 2026-10-03T10:15:30
      0 0 9 1 * ?            | 2026-11-01T09:00
      0 0 12 L * ?           | 2026-10-31T12:00
      0 0 10 ? * 6L          | 2026-10-30T10:00
      0 0 10 ? * 2#1         | 2026-10-05T10:00
      0 0 9 ? * SAT,SUN      | 2026-10-04T09:00
      0 30 18 LW * ?         | 2026-10-30T18:30
      """)
  void nextFireTime(String expression, String expected) throws Exception {
    CronExpression cron = new CronExpression(expression);
    cron.setTimeZone(TimeZone.getTimeZone(ZONE));
    Date next = cron.getNextValidTimeAfter(Date.from(NOW.atZone(ZONE).toInstant()));
    assertThat(LocalDateTime.ofInstant(next.toInstant(), ZONE)).isEqualTo(LocalDateTime.parse(expected));
  }

  @Test
  void quartzAndSpringNumberTheDaysOfTheWeekDifferently() {
    // Quartz: 1 = SUN ... 6 = FRI.  Spring @Scheduled: 0 or 7 = SUN, 1 = MON ... 5 = FRI
    LocalDateTime quartz6 = nextQuartz("0 0 10 ? * 6");
    LocalDateTime spring6 = org.springframework.scheduling.support.CronExpression.parse("0 0 10 * * 6").next(NOW);
    System.out.println("Quartz '6' -> " + quartz6.getDayOfWeek() + ", Spring '6' -> " + spring6.getDayOfWeek());
    assertThat(quartz6).isEqualTo(LocalDateTime.of(2026, 10, 9, 10, 0));    // Friday
    assertThat(spring6).isEqualTo(LocalDateTime.of(2026, 10, 3, 10, 0).plusDays(7)); // Saturday
  }

  @Test
  void quartzNeedsAQuestionMarkInOneDayField() {
    assertThatThrownBy(() -> new CronExpression("0 0 8 * * MON-FRI"))
        .hasMessage("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
    assertThatThrownBy(() -> new CronExpression("0 8 * * *"))
        .hasMessageStartingWith("Unexpected end of expression");
    assertThat(CronExpression.isValidExpression("0 0 8 ? * MON-FRI")).isTrue();
  }

  private static LocalDateTime nextQuartz(String expression) {
    try {
      CronExpression cron = new CronExpression(expression);
      cron.setTimeZone(TimeZone.getTimeZone(ZONE));
      return LocalDateTime.ofInstant(cron.getNextValidTimeAfter(Date.from(NOW.atZone(ZONE).toInstant())).toInstant(), ZONE);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
