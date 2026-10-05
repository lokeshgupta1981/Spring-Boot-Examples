# Spring Boot: Import Excel File into Database (Apache POI)

Source code for the article [Spring Boot: Import Excel File into Database (Apache POI)](https://howtodoinjava.com/?p=26936).

A school uploads a class roster (roll number, name, class, email, marks, admission date) as an `.xlsx` file.
`POST /students/import` reads it with Apache POI, validates each row with Bean Validation, reports row errors
as JSON and saves the valid rows in JDBC batches.

## Versions

- Java 25
- Spring Boot 4.1.1 (Spring Framework 7.0.9, Hibernate 7.4.5.Final, H2 2.4.240)
- Apache POI 5.5.1 (`poi-ooxml`)
- JUnit 6.1.3

## Run

```bash
mvn spring-boot:run

# import the sample roster (8 rows, 4 valid)
curl -F "file=@samples/students.xlsx" http://localhost:8080/students/import

# same file with the SAX (XSSF event API) reader, and as all-or-nothing
curl -F "file=@samples/students.xlsx" "http://localhost:8080/students/import?streaming=true"
curl -F "file=@samples/students.xlsx" "http://localhost:8080/students/import?allOrNothing=true"

# saved students
curl http://localhost:8080/students
```

Tests:

```bash
mvn test
```

Memory comparison of the two readers (generates a 100,000-row file in `target/`, takes several minutes):

```bash
mvn -q test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt -Dmdep.includeScope=test
java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" com.howtodoinjava.excelimport.ReaderMemoryComparison 100000
```

Regenerate the sample file:

```bash
java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" com.howtodoinjava.excelimport.SampleFileGenerator samples/students.xlsx
```

## Files

| File | What it does |
|---|---|
| `student/StudentImportController.java` | `POST /students/import` (multipart), `GET /students` |
| `student/StudentImportService.java` | Header mapping, conversion, validation, duplicate checks, chunked `saveAll()` |
| `student/StudentRow.java` | One converted row with Bean Validation constraints |
| `student/StudentColumn.java` | Header text to field name |
| `student/Student.java`, `StudentRepository.java` | JPA entity with unique `rollNumber`, repository |
| `student/ImportReport.java`, `RowError.java` | JSON report |
| `student/ImportExceptionHandler.java` | 400 and 413 as `ProblemDetail` |
| `excel/WorkbookSheetReader.java` | Reads the sheet with `XSSFWorkbook` |
| `excel/StreamingSheetReader.java` | Reads the sheet with the XSSF event API (SAX) |
| `excel/IsoDateFormatter.java` | `DataFormatter` that returns dates as `2024-06-15` |
| `src/main/resources/application.properties` | Upload limits, JDBC batch size, session metrics log |
| `samples/students.xlsx` | Sample roster for curl |
| `test/.../StudentImportControllerTest.java` | MockMvc tests with generated `.xlsx` files |
| `test/.../UploadLimitTest.java` | 413 for a 6 MB upload against the real Tomcat |
| `test/.../CellReadingTest.java` | `DataFormatter` results for numeric, date, formula and missing cells |
| `test/.../ReaderMemoryComparison.java` | Smallest heap for each reader on a large file |
