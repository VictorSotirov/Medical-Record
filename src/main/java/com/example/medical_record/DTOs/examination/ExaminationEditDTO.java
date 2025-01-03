package com.example.medical_record.DTOs.examination;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExaminationEditDTO
{
    @NotNull(message = "Examination date cannot be null.")
    private LocalDate examinationDate;

    @NotBlank(message = "Treatment description cannot be null.")
    private String treatmentDescription;

    @NotNull(message = "Sick leave duration cannot be null.")
    private int sickLeaveDays;

    @NotNull(message = "Sick leave start date cannot be null.")
    private LocalDate sickLeaveStartDate;

    @NotNull(message = "Doctor cannot be null.")
    private Long doctorId;

    @NotNull(message = "Diagnosis cannot be null.")
    private Long diagnosisId;
}
