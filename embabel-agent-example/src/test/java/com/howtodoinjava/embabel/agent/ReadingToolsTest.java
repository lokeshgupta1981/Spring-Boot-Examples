package com.howtodoinjava.embabel.agent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReadingToolsTest {

  private final ReadingTools tools = new ReadingTools();

  @Test
  void roundsPagesPerDayUp() {
    assertThat(tools.calculateDailyPages(310, 10)).isEqualTo(31);
    assertThat(tools.calculateDailyPages(541, 10)).isEqualTo(55);
  }

  @Test
  void returnsZeroForInvalidInput() {
    assertThat(tools.calculateDailyPages(310, 0)).isZero();
    assertThat(tools.calculateDailyPages(-5, 10)).isZero();
  }
}
