package com.howtodoinjava.excelimport.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reads the sheet XML with SAX (the XSSF event API). Only the current row is kept in memory,
 * so the heap stays small for files with hundreds of thousands of rows.
 */
public class StreamingSheetReader implements SheetReader {

  @Override
  public void read(OPCPackage pkg, Consumer<SheetRow> rows) throws IOException {
    try {
      XSSFReader reader = new XSSFReader(pkg);
      ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
      try (InputStream sheet = reader.getSheetIterator().next()) {        // first sheet
        XMLReader parser = XMLHelper.newXMLReader();
        parser.setContentHandler(new XSSFSheetXMLHandler(
            reader.getStylesTable(), strings, new RowCollector(rows), new IsoDateFormatter(), false));
        parser.parse(new InputSource(sheet));
      }
    } catch (OpenXML4JException | SAXException | ParserConfigurationException e) {
      throw new IOException("Not a readable .xlsx file", e);
    }
  }

  /** Receives cells one by one; empty cells are not reported, so we fill the gaps by column index. */
  private static class RowCollector implements SheetContentsHandler {

    private final Consumer<SheetRow> rows;
    private List<String> values;

    RowCollector(Consumer<SheetRow> rows) {
      this.rows = rows;
    }

    @Override
    public void startRow(int rowNum) {
      values = new ArrayList<>();
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
      int column = new CellReference(cellReference).getCol();
      while (values.size() < column) {
        values.add("");
      }
      values.add(formattedValue == null ? "" : formattedValue.trim());
    }

    @Override
    public void endRow(int rowNum) {
      if (!SheetReader.isBlank(values)) {
        rows.accept(new SheetRow(rowNum + 1, values));
      }
    }
  }
}
