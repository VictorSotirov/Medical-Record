package com.example.medical_record.DTOs.patient;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDTO
{
    private Long id;

    private String firstName;

    private String lastName;

    private boolean isHealthInsurancePaid;

    private DoctorResponseDTO personalDoctor;
}
