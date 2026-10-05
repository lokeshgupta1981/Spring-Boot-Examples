package com.howtodoinjava.excelimport;

import java.nio.file.Files;
import java.nio.file.Path;

/** Writes samples/students.xlsx, the class roster used in the README curl example. */
public class SampleFileGenerator {

  public static void main(String[] args) throws Exception {
    Path target = Path.of(args.length > 0 ? args[0] : "samples/students.xlsx");
    Files.createDirectories(target.getParent());
    Files.write(target, TestWorkbooks.xlsx(TestWorkbooks.roster()));
    System.out.println("Wrote " + target.toAbsolutePath());
  }
}
