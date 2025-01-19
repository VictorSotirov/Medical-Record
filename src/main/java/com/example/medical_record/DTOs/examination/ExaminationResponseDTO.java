package com.example.medical_record.DTOs.examination;

import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExaminationResponseDTO
{
    private Long id;

    private LocalDate examinationDate;

    private String treatmentDescription;

    private int sickLeaveDays;

    private LocalDate sickLeaveStartDate;

    private PatientResponseDTO patient;

    private DoctorResponseDTO doctor;

    private DiagnosisResponseDTO diagnosis;
}
