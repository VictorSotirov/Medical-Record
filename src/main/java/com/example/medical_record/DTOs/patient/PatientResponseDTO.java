package com.example.medical_record.DTOs.patient;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDTO
{
    private Long id;

    private String firstName;

    private String lastName;

    private boolean isHealthInsurancePaid;

    private DoctorResponseDTO personalDoctor;
}
