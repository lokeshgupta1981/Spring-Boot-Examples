package com.howtodoinjava.excelimport.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * A DataFormatter that returns the text Excel shows for every cell, except dates:
 * a date cell becomes an ISO date such as 2024-06-15, whatever display format the sheet uses.
 */
public class IsoDateFormatter extends DataFormatter {

  // Used by the XSSF event API (streaming reader) for numeric cells
  @Override
  public String formatRawCellContents(double value, int formatIndex, String formatString,
      boolean use1904Windowing) {
    if (DateUtil.isADateFormat(formatIndex, formatString) && DateUtil.isValidExcelDate(value)) {
      return DateUtil.getLocalDateTime(value, use1904Windowing).toLocalDate().toString();
    }
    return super.formatRawCellContents(value, formatIndex, formatString, use1904Windowing);
  }

  // Used by XSSFWorkbook (in-memory reader)
  @Override
  public String formatCellValue(Cell cell) {
    if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
      return cell.getLocalDateTimeCellValue().toLocalDate().toString();
    }
    return super.formatCellValue(cell).trim();
  }
}
