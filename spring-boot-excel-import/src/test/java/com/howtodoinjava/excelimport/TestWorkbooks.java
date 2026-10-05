package com.howtodoinjava.excelimport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Builds .xlsx files with POI for the tests and the sample file. */
public final class TestWorkbooks {

  public static final Object[] HEADER =
      {"Roll No", "Name", "Class", "Email", "Marks", "Admission Date"};

  private TestWorkbooks() {
  }

  /** The class roster used in the article: 8 data rows, one blank row, 4 valid rows. */
  public static List<Object[]> roster() {
    List<Object[]> rows = new ArrayList<>();
    rows.add(HEADER);
    rows.add(new Object[]{101, "Anna", "7A", "anna@school.com", 85, LocalDate.of(2024, 6, 15)});
    rows.add(new Object[]{102, "Ben", "7A", "ben@school.com", 92, LocalDate.of(2024, 6, 15)});
    rows.add(new Object[]{"", "", "", "", "", ""});                       // blank row
    rows.add(new Object[]{103, "Chloe", "7A", "chloe.school.com", 78, LocalDate.of(2024, 6, 17)});
    rows.add(new Object[]{104, "David", "7A", "david@school.com", 105, "15/06/2024"});
    rows.add(new Object[]{102, "Emma", "7A", "emma@school.com", 88, LocalDate.of(2024, 6, 18)});
    rows.add(new Object[]{105, "", "7A", "farah@school.com", "absent", LocalDate.of(2024, 6, 18)});
    rows.add(new Object[]{106, "Grace", "7B", "grace@school.com", 95, LocalDate.of(2024, 6, 14)});
    rows.add(new Object[]{107, "Hugo", "7B", "hugo@school.com", 67, LocalDate.of(2024, 6, 20)});
    return rows;
  }

  /** A header and the given number of valid rows, roll numbers starting at 1. */
  public static List<Object[]> validRows(int count) {
    List<Object[]> rows = new ArrayList<>();
    rows.add(HEADER);
    for (int i = 1; i <= count; i++) {
      rows.add(new Object[]{i, "Student " + i, "7A", "s" + i + "@school.com", i % 101,
          LocalDate.of(2024, 6, 15)});
    }
    return rows;
  }

  public static byte[] xlsx(List<Object[]> rows) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      fill(workbook.createSheet("Students"), dateStyle(workbook), rows);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    }
  }

  /** Writes a large file with SXSSFWorkbook, which keeps only 100 rows in memory while writing. */
  public static void writeLarge(List<Object[]> rows, OutputStream out) throws IOException {
    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
      fill(workbook.createSheet("Students"), dateStyle(workbook), rows);
      workbook.write(out);
    }
  }

  private static CellStyle dateStyle(org.apache.poi.ss.usermodel.Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setDataFormat((short) 14);                  // built-in format m/d/yy
    return style;
  }

  private static void fill(Sheet sheet, CellStyle dateStyle, List<Object[]> rows) {
    for (int r = 0; r < rows.size(); r++) {
      Row row = sheet.createRow(r);
      Object[] values = rows.get(r);
      for (int c = 0; c < values.length; c++) {
        Cell cell = row.createCell(c);
        switch (values[c]) {
          case Number n -> cell.setCellValue(n.doubleValue());
          case LocalDate d -> {
            cell.setCellValue(d);
            cell.setCellStyle(dateStyle);
          }
          default -> cell.setCellValue(values[c].toString());
        }
      }
    }
  }
}
