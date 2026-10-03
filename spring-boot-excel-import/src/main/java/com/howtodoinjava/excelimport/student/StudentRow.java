package com.howtodoinjava.excelimport.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** One sheet row after type conversion, checked with Bean Validation before it becomes a Student. */
public record StudentRow(
    int rowNumber,
    @NotBlank @Pattern(regexp = "\\d{1,6}", message = "must be a number with up to 6 digits") String rollNumber,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email String email,
    @NotBlank String className,
    @NotNull @Min(0) @Max(100) Integer marks,
    @NotNull @PastOrPresent LocalDate admissionDate) {

  public Student toEntity() {
    return new Student(rollNumber, name, email, className, marks, admissionDate);
  }
}
