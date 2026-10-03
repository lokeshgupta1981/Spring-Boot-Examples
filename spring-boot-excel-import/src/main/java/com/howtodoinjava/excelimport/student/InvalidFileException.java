package com.howtodoinjava.excelimport.student;

/** The uploaded file cannot be imported at all: not an .xlsx file, or required columns are missing. */
public class InvalidFileException extends RuntimeException {

  public InvalidFileException(String message) {
    super(message);
  }
}
