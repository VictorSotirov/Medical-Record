package com.example.medical_record.DTOs.hospitalRecord;

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
public class HospitalRecordResponseDTO
{
    private Long id;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private PatientResponseDTO patient;

    private DoctorResponseDTO doctor;
}
