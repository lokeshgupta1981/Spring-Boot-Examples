package com.howtodoinjava.excelimport.excel;

import java.util.List;

/** The text of one non-blank row. rowNumber is the 1-based number Excel shows; values are indexed by column. */
public record SheetRow(int rowNumber, List<String> values) {

  public String value(int columnIndex) {
    return columnIndex < values.size() ? values.get(columnIndex) : "";
  }
}
