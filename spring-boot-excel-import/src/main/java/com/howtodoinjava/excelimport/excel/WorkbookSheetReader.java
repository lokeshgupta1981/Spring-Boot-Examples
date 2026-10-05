package com.howtodoinjava.excelimport.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Loads the whole workbook into memory with XSSFWorkbook. Simple, fine for sheets with a few thousand rows. */
public class WorkbookSheetReader implements SheetReader {

  private final DataFormatter formatter = new IsoDateFormatter();

  @Override
  public void read(OPCPackage pkg, Consumer<SheetRow> rows) throws IOException {
    XSSFWorkbook workbook = new XSSFWorkbook(pkg);   // the caller closes the package
    Sheet sheet = workbook.getSheetAt(0);
    for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;                                    // row never written
      }
      List<String> values = new ArrayList<>();
      for (int c = 0; c < row.getLastCellNum(); c++) {
        values.add(formatter.formatCellValue(row.getCell(c)));   // "" for a missing cell
      }
      if (!SheetReader.isBlank(values)) {
        rows.accept(new SheetRow(r + 1, values));
      }
    }
  }
}
