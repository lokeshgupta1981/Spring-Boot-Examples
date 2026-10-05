package com.howtodoinjava.excelimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.howtodoinjava.excelimport.student.Student;
import com.howtodoinjava.excelimport.student.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class StudentImportControllerTest {

  static final String XLSX =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  static final String ROSTER_REPORT = """
      {"totalRows":8,"imported":4,"failed":4,"errors":[
        {"row":5,"column":"Email","message":"must be a well-formed email address"},
        {"row":6,"column":"Marks","message":"must be less than or equal to 100"},
        {"row":6,"column":"Admission Date","message":"must be a date such as 2024-06-15"},
        {"row":7,"column":"Roll No","message":"duplicate of row 3"},
        {"row":8,"column":"Name","message":"must not be blank"},
        {"row":8,"column":"Marks","message":"must be a whole number"}]}
      """;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  StudentRepository repository;

  @BeforeEach
  void cleanDatabase() {
    repository.deleteAllInBatch();
  }

  static MockMultipartFile file(byte[] bytes) {
    return new MockMultipartFile("file", "class-7.xlsx", XLSX, bytes);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void importsValidRowsAndReportsRowErrors(boolean streaming) throws Exception {
    byte[] roster = TestWorkbooks.xlsx(TestWorkbooks.roster());

    mockMvc.perform(multipart("/students/import")
            .file(file(roster))
            .param("streaming", String.valueOf(streaming)))
        .andExpect(status().isOk())
        .andExpect(content().json(ROSTER_REPORT, JsonCompareMode.STRICT));

    List<Student> saved = repository.findAllByOrderByRollNumber();
    assertThat(saved).extracting(Student::getRollNumber).containsExactly("101", "102", "106", "107");
    Student anna = saved.getFirst();
    assertThat(anna.getName()).isEqualTo("Anna");
    assertThat(anna.getEmail()).isEqualTo("anna@school.com");
    assertThat(anna.getClassName()).isEqualTo("7A");
    assertThat(anna.getMarks()).isEqualTo(85);
    assertThat(anna.getAdmissionDate()).isEqualTo(LocalDate.of(2024, 6, 15));
  }

  @Test
  void secondUploadReportsExistingRollNumbers() throws Exception {
    byte[] roster = TestWorkbooks.xlsx(TestWorkbooks.roster());
    mockMvc.perform(multipart("/students/import").file(file(roster))).andExpect(status().isOk());

    String body = mockMvc.perform(multipart("/students/import").file(file(roster)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.imported").value(0))
        .andExpect(jsonPath("$.failed").value(8))
        .andExpect(jsonPath("$.errors[0].row").value(2))
        .andExpect(jsonPath("$.errors[0].column").value("Roll No"))
        .andExpect(jsonPath("$.errors[0].message").value("already exists"))
        .andReturn().getResponse().getContentAsString();
    System.out.println("SECOND UPLOAD " + body);
    assertThat(repository.count()).isEqualTo(4);
  }

  @Test
  void allOrNothingSavesNothingWhenOneRowFails() throws Exception {
    byte[] roster = TestWorkbooks.xlsx(TestWorkbooks.roster());

    mockMvc.perform(multipart("/students/import").file(file(roster)).param("allOrNothing", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.imported").value(0))
        .andExpect(jsonPath("$.failed").value(4));

    assertThat(repository.count()).isZero();
  }

  @Test
  void savesRowsInJdbcBatches(CapturedOutput output) throws Exception {
    byte[] file = TestWorkbooks.xlsx(TestWorkbooks.validRows(1200));

    mockMvc.perform(multipart("/students/import").file(file(file)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.imported").value(1200))
        .andExpect(jsonPath("$.errors").isEmpty());

    assertThat(repository.count()).isEqualTo(1200);
    Matcher batches = Pattern.compile("executing (\\d+) JDBC batches").matcher(output.getOut());
    int max = 0;
    while (batches.find()) {
      max = Math.max(max, Integer.parseInt(batches.group(1)));
    }
    assertThat(max).isEqualTo(24);                      // 1,200 inserts / batch_size 50
  }

  @Test
  void missingColumnRejectsTheFile() throws Exception {
    List<Object[]> rows = List.of(
        new Object[]{"Roll No", "Name", "Class", "Marks", "Admission Date"},
        new Object[]{101, "Anna", "7A", 85, LocalDate.of(2024, 6, 15)});

    mockMvc.perform(multipart("/students/import").file(file(TestWorkbooks.xlsx(rows))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Missing columns: Email"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void fileThatIsNotExcelIsRejected(boolean streaming) throws Exception {
    MockMultipartFile csv = new MockMultipartFile("file", "class-7.xlsx", XLSX,
        "Roll No,Name\n101,Anna\n".getBytes());

    mockMvc.perform(multipart("/students/import").file(csv).param("streaming", String.valueOf(streaming)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Not a readable .xlsx file"));
  }

  @Test
  void mockMvcDoesNotApplyTheUploadLimit() throws Exception {
    byte[] sixMegabytes = new byte[6 * 1024 * 1024];       // over max-file-size=5MB

    mockMvc.perform(multipart("/students/import").file(file(sixMegabytes)))
        .andExpect(status().isBadRequest())                 // reaches the controller, no 413
        .andExpect(jsonPath("$.detail").value("Not a readable .xlsx file"));
  }

  @Test
  void otherFileExtensionIsRejected() throws Exception {
    MockMultipartFile csv = new MockMultipartFile("file", "class-7.csv", "text/csv",
        "Roll No,Name\n101,Anna\n".getBytes());

    mockMvc.perform(multipart("/students/import").file(csv))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Upload a non-empty .xlsx file"));
  }
}
