package com.example.medical_record.DTOs.doctor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorResponseDTO
{
    private Long id;

    private String firstName;

    private String lastName;

    private String specialty;

    //TODO POTENIALLY ADD OTHER ENTITIES CONNECTED TO DOCTOR
}
