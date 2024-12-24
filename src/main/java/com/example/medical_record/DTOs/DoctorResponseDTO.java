package com.example.medical_record.DTOs;

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

    private Boolean isDeleted;

    //TODO POTENIALLY ADD OTHER ENTITIES CONNECTED TO DOCTOR
}
