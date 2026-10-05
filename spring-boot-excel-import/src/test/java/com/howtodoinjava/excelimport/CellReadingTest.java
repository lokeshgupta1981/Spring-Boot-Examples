package com.howtodoinjava.excelimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.howtodoinjava.excelimport.excel.IsoDateFormatter;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

/** Checks every cell-reading result shown in the article. */
class CellReadingTest {

  @Test
  void dataFormatterReturnsWhatExcelShows() throws Exception {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      Row row = workbook.createSheet().createRow(0);
      Cell roll = row.createCell(0);
      roll.setCellValue(101);                            // a numeric cell
      Cell marks = row.createCell(1);
      marks.setCellValue(85.5);
      Cell date = row.createCell(2);
      date.setCellValue(LocalDate.of(2024, 6, 15));
      CellStyle style = workbook.createCellStyle();
      style.setDataFormat((short) 14);
      date.setCellStyle(style);
      Cell formula = row.createCell(3);
      formula.setCellFormula("B1*2");
      Cell textDate = row.createCell(4);
      textDate.setCellValue("2024-06-15");

      assertThatThrownBy(roll::getStringCellValue)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("Cannot get a STRING value from a NUMERIC cell");
      assertThat(roll.getNumericCellValue()).isEqualTo(101.0);

      DataFormatter formatter = new DataFormatter();
      System.out.println("roll=" + formatter.formatCellValue(roll) + " marks="
          + formatter.formatCellValue(marks) + " date=" + formatter.formatCellValue(date)
          + " formula=" + formatter.formatCellValue(formula) + " missing=["
          + formatter.formatCellValue(row.getCell(9)) + "]");
      assertThat(formatter.formatCellValue(roll)).isEqualTo("101");
      assertThat(formatter.formatCellValue(marks)).isEqualTo("85.5");
      assertThat(formatter.formatCellValue(date)).isEqualTo("6/15/24");
      assertThat(formatter.formatCellValue(formula)).isEqualTo("B1*2");
      assertThat(formatter.formatCellValue(row.getCell(9))).isEqualTo("");

      var evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      assertThat(formatter.formatCellValue(formula, evaluator)).isEqualTo("171");

      DataFormatter iso = new IsoDateFormatter();
      assertThat(iso.formatCellValue(date)).isEqualTo("2024-06-15");
      assertThat(iso.formatCellValue(textDate)).isEqualTo("2024-06-15");
      assertThat(iso.formatCellValue(roll)).isEqualTo("101");
    }
  }
}
