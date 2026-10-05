package com.howtodoinjava.excelimport.student;

import com.howtodoinjava.excelimport.excel.SheetReader;
import com.howtodoinjava.excelimport.excel.StreamingSheetReader;
import com.howtodoinjava.excelimport.excel.WorkbookSheetReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.poi.UnsupportedFileFormatException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class StudentImportController {

  private final StudentImportService importService;
  private final StudentRepository repository;
  private final SheetReader workbookReader = new WorkbookSheetReader();
  private final SheetReader streamingReader = new StreamingSheetReader();

  public StudentImportController(StudentImportService importService, StudentRepository repository) {
    this.importService = importService;
    this.repository = repository;
  }

  @PostMapping(value = "/students/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ImportReport importStudents(@RequestParam("file") MultipartFile file,
      @RequestParam(defaultValue = "false") boolean streaming,
      @RequestParam(defaultValue = "false") boolean allOrNothing) {

    String fileName = file.getOriginalFilename();
    if (file.isEmpty() || fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
      throw new InvalidFileException("Upload a non-empty .xlsx file");
    }
    SheetReader reader = streaming ? streamingReader : workbookReader;
    Path upload = null;
    try {
      upload = Files.createTempFile("students-", ".xlsx");
      file.transferTo(upload);                     // POI reads a file part by part
      return importService.importStudents(upload, reader, allOrNothing);
    } catch (IOException | UnsupportedFileFormatException e) {
      throw new InvalidFileException("Not a readable .xlsx file");
    } finally {
      deleteQuietly(upload);
    }
  }

  private static void deleteQuietly(Path path) {
    try {
      if (path != null) {
        Files.deleteIfExists(path);
      }
    } catch (IOException ignored) {
      // the temp directory is cleaned by the OS
    }
  }

  @GetMapping("/students")
  public List<Student> students() {
    return repository.findAllByOrderByRollNumber();
  }
}
