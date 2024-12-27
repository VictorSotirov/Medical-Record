package com.example.medical_record.DTOs.hospitalRecord;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HospitalRecordResponseDTO
{
    private Long id;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private PatientResponseDTO patient;

    private DoctorResponseDTO doctor;
}
