package com.howtodoinjava.excelimport;

import com.howtodoinjava.excelimport.excel.SheetReader;
import com.howtodoinjava.excelimport.excel.StreamingSheetReader;
import com.howtodoinjava.excelimport.excel.WorkbookSheetReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compares the heap needed by XSSFWorkbook and by the XSSF event API (SAX) for one large file.
 * Each reader runs in a fresh JVM with a growing -Xmx until it reads the whole file.
 */
public class ReaderMemoryComparison {

  static final int[] HEAP_MB = {16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536};

  public static void main(String[] args) throws Exception {
    if (args.length == 2) {
      readOnce(args[0], Path.of(args[1]));
      return;
    }
    int rows = args.length > 0 ? Integer.parseInt(args[0]) : 100_000;
    Path file = Path.of("target", "students-" + rows + ".xlsx");
    try (OutputStream out = Files.newOutputStream(file)) {
      TestWorkbooks.writeLarge(TestWorkbooks.validRows(rows), out);
    }
    System.out.printf("File: %s, %,d rows, %,d KB%n", file, rows, Files.size(file) / 1024);
    for (String reader : List.of("streaming-file", "streaming-stream", "workbook-file", "workbook-stream")) {
      for (int heap : HEAP_MB) {
        Result result = runChild(reader, heap, file);
        if (result.ok()) {
          System.out.printf("%-17s smallest working -Xmx: %4d MB%n", reader, heap);
          break;
        }
      }
      System.out.printf("%-17s with -Xmx2g: %s%n", reader, runChild(reader, 2048, file).line());
    }
  }

  record Result(boolean ok, String line) {
  }

  static Result runChild(String reader, int heapMb, Path file) throws Exception {
    List<String> command = new ArrayList<>(List.of(
        Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-Xmx" + heapMb + "m", "-XX:+UseSerialGC",
        "-cp", System.getProperty("java.class.path"),
        ReaderMemoryComparison.class.getName(), reader, file.toString()));
    Path log = Files.createTempFile(Path.of("target"), "reader-", ".log");
    Process process = new ProcessBuilder(command).redirectErrorStream(true)
        .redirectOutput(log.toFile()).start();
    if (!process.waitFor(2, java.util.concurrent.TimeUnit.MINUTES)) {
      process.destroyForcibly().waitFor();          // too slow: the heap is too small
      return new Result(false, "timeout");
    }
    int exit = process.exitValue();
    String output = Files.readString(log).trim();
    String last = output.isEmpty() ? "" : output.substring(output.lastIndexOf('\n') + 1);
    return new Result(exit == 0 && last.startsWith("rows="), last);
  }

  static void readOnce(String readerName, Path file) throws Exception {
    SheetReader reader = readerName.startsWith("workbook")
        ? new WorkbookSheetReader() : new StreamingSheetReader();
    AtomicInteger count = new AtomicInteger();
    long start = System.nanoTime();
    if (readerName.endsWith("-file")) {
      reader.read(file, row -> count.incrementAndGet());
    } else {
      try (InputStream in = Files.newInputStream(file)) {
        reader.read(in, row -> count.incrementAndGet());
      }
    }
    long millis = (System.nanoTime() - start) / 1_000_000;
    System.out.printf("rows=%d (header included), time %d ms%n", count.get(), millis);
  }
}
