package com.howtodoinjava.excelimport.student;

import java.util.List;

/** The JSON answer of POST /students/import. */
public record ImportReport(int totalRows, int imported, int failed, List<RowError> errors) {
}
