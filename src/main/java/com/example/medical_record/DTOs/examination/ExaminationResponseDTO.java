package com.example.medical_record.DTOs.examination;

import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
