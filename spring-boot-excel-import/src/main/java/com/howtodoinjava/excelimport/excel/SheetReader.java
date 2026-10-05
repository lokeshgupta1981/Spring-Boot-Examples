package com.howtodoinjava.excelimport.excel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;

/** Reads the first sheet of an .xlsx file and passes each non-blank row, header included, to the consumer. */
public interface SheetReader {

  void read(OPCPackage pkg, Consumer<SheetRow> rows) throws IOException;

  /** Opens the zip from a file: POI reads each part on demand. */
  default void read(Path file, Consumer<SheetRow> rows) throws IOException {
    try (OPCPackage pkg = OPCPackage.open(file.toFile(), PackageAccess.READ)) {
      read(pkg, rows);
    } catch (InvalidFormatException e) {
      throw new IOException("Not a readable .xlsx file", e);
    }
  }

  /** Opens the zip from a stream: POI unzips every part into memory first. */
  default void read(InputStream in, Consumer<SheetRow> rows) throws IOException {
    try (OPCPackage pkg = OPCPackage.open(in)) {
      read(pkg, rows);
    } catch (InvalidFormatException e) {
      throw new IOException("Not a readable .xlsx file", e);
    }
  }

  static boolean isBlank(List<String> values) {
    return values.stream().allMatch(String::isBlank);
  }
}
