package com.example.medical_record.DTOs.doctor;

import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorWithPatientsDTO
{
    private DoctorResponseDTO doctor;

    private List<PatientResponseDTO> patients;
}
