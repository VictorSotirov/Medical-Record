package com.example.medical_record.DTOs.hospitalRecord;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HospitalRecordEditDTO
{
    @NotNull(message = "Admission date cannot be null.")
    private LocalDate admissionDate;

    @NotNull(message = "Discharge date cannot be null.")
    private LocalDate dischargeDate;

    @NotNull(message = "Doctor ID cannot be null.")
    private Long doctorId;
}
