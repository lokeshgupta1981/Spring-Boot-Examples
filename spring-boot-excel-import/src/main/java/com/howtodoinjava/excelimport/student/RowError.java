package com.howtodoinjava.excelimport.student;

/** A problem in one cell: the Excel row number, the column header and the message. */
public record RowError(int row, String column, String message) {
}
