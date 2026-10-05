package com.howtodoinjava.embabel.agent;

import com.embabel.agent.api.annotation.LlmTool;

public class ReadingTools {

  @LlmTool(description = "Calculates how many pages per day are needed to finish a book in the given number of days")
  public int calculateDailyPages(
      @LlmTool.Param(description = "total pages of the book") int pages,
      @LlmTool.Param(description = "number of days to finish the book") int days) {
    if (pages <= 0 || days <= 0) {
      return 0;
    }
    return (pages + days - 1) / days;
  }
}
