package com.howtodoinjava.excelimport.student;

import com.howtodoinjava.excelimport.excel.SheetReader;
import com.howtodoinjava.excelimport.excel.SheetRow;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class StudentImportService {

  static final int CHUNK_SIZE = 500;

  private final StudentRepository repository;
  private final Validator validator;
  private final EntityManager entityManager;

  public StudentImportService(StudentRepository repository, Validator validator,
      EntityManager entityManager) {
    this.repository = repository;
    this.validator = validator;
    this.entityManager = entityManager;
  }

  @Transactional
  public ImportReport importStudents(Path file, SheetReader reader, boolean allOrNothing)
      throws IOException {
    ImportRun run = new ImportRun();
    reader.read(file, run::accept);
    run.saveChunk();                                    // the last, partly filled chunk

    if (run.header == null) {
      throw new InvalidFileException("The sheet is empty");
    }
    int imported = run.imported;
    if (allOrNothing && !run.errors.isEmpty()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      imported = 0;
    }
    List<RowError> errors = run.errors.stream()
        .sorted(Comparator.comparingInt(RowError::row).thenComparing(e -> columnOrder(e.column())))
        .toList();
    int failed = (int) errors.stream().map(RowError::row).distinct().count();
    return new ImportReport(run.totalRows, imported, failed, errors);
  }

  /** State of one import: header positions, roll numbers seen so far, the chunk waiting to be saved. */
  private class ImportRun {

    Map<StudentColumn, Integer> header;
    final Map<String, Integer> seenRollNumbers = new HashMap<>();
    final List<StudentRow> chunk = new ArrayList<>();
    final List<RowError> errors = new ArrayList<>();
    int totalRows;
    int imported;

    void accept(SheetRow row) {
      if (header == null) {
        header = mapHeader(row);
        return;
      }
      totalRows++;
      StudentRow student = toStudentRow(row);
      if (student == null) {
        return;                                         // conversion or validation errors
      }
      Integer firstRow = seenRollNumbers.putIfAbsent(student.rollNumber(), row.rowNumber());
      if (firstRow != null) {
        errors.add(new RowError(row.rowNumber(), StudentColumn.ROLL_NUMBER.header(),
            "duplicate of row " + firstRow));
        return;
      }
      chunk.add(student);
      if (chunk.size() == CHUNK_SIZE) {
        saveChunk();
      }
    }

    Map<StudentColumn, Integer> mapHeader(SheetRow row) {
      Map<String, Integer> positions = new HashMap<>();
      for (int i = 0; i < row.values().size(); i++) {
        positions.put(row.value(i).trim().toLowerCase(), i);
      }
      Map<StudentColumn, Integer> columns = new EnumMap<>(StudentColumn.class);
      List<String> missing = new ArrayList<>();
      for (StudentColumn column : StudentColumn.values()) {
        Integer index = positions.get(column.header().toLowerCase());
        if (index == null) {
          missing.add(column.header());
        } else {
          columns.put(column, index);
        }
      }
      if (!missing.isEmpty()) {
        throw new InvalidFileException("Missing columns: " + String.join(", ", missing));
      }
      return columns;
    }

    StudentRow toStudentRow(SheetRow row) {
      int before = errors.size();
      Set<String> badColumns = new HashSet<>();

      Integer marks = null;
      String marksText = text(row, StudentColumn.MARKS);
      if (marksText != null) {
        try {
          marks = Integer.valueOf(marksText);
        } catch (NumberFormatException e) {
          badColumns.add(addError(row, StudentColumn.MARKS, "must be a whole number"));
        }
      }
      LocalDate admissionDate = null;
      String dateText = text(row, StudentColumn.ADMISSION_DATE);
      if (dateText != null) {
        try {
          admissionDate = LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
          badColumns.add(addError(row, StudentColumn.ADMISSION_DATE,
              "must be a date such as 2024-06-15"));
        }
      }

      StudentRow student = new StudentRow(row.rowNumber(),
          text(row, StudentColumn.ROLL_NUMBER), text(row, StudentColumn.NAME),
          text(row, StudentColumn.EMAIL), text(row, StudentColumn.CLASS_NAME),
          marks, admissionDate);

      for (ConstraintViolation<StudentRow> violation : validator.validate(student)) {
        String column = StudentColumn.headerOf(violation.getPropertyPath().toString());
        if (!badColumns.contains(column)) {
          errors.add(new RowError(row.rowNumber(), column, violation.getMessage()));
        }
      }
      return errors.size() == before ? student : null;
    }

    String text(SheetRow row, StudentColumn column) {
      String value = row.value(header.get(column));
      return value.isBlank() ? null : value;
    }

    String addError(SheetRow row, StudentColumn column, String message) {
      errors.add(new RowError(row.rowNumber(), column.header(), message));
      return column.header();
    }

    void saveChunk() {
      if (chunk.isEmpty()) {
        return;
      }
      Set<String> existing = new HashSet<>(repository.findExistingRollNumbers(
          chunk.stream().map(StudentRow::rollNumber).toList()));
      List<Student> students = new ArrayList<>();
      for (StudentRow row : chunk) {
        if (existing.contains(row.rollNumber())) {
          errors.add(new RowError(row.rowNumber(), StudentColumn.ROLL_NUMBER.header(),
              "already exists"));
        } else {
          students.add(row.toEntity());
        }
      }
      repository.saveAll(students);   // persist() per student, sent in JDBC batches of 50
      entityManager.flush();          // run the batched inserts now
      entityManager.clear();          // free the saved entities before the next chunk
      imported += students.size();
      chunk.clear();
    }
  }

  private static int columnOrder(String header) {
    for (StudentColumn column : StudentColumn.values()) {
      if (column.header().equals(header)) {
        return column.ordinal();
      }
    }
    return Integer.MAX_VALUE;
  }
}
