package com.howtodoinjava.excelimport.student;

/** Maps a sheet header to a field of StudentRow. The order of the columns in the file does not matter. */
public enum StudentColumn {
  ROLL_NUMBER("Roll No", "rollNumber"),
  NAME("Name", "name"),
  EMAIL("Email", "email"),
  CLASS_NAME("Class", "className"),
  MARKS("Marks", "marks"),
  ADMISSION_DATE("Admission Date", "admissionDate");

  private final String header;
  private final String field;

  StudentColumn(String header, String field) {
    this.header = header;
    this.field = field;
  }

  public String header() { return header; }
  public String field() { return field; }

  public static String headerOf(String field) {
    for (StudentColumn column : values()) {
      if (column.field.equals(field)) {
        return column.header;
      }
    }
    return field;
  }
}
