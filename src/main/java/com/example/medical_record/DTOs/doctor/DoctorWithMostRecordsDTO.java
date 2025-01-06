package com.example.medical_record.DTOs.doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DoctorWithMostRecordsDTO
{
    private Long doctorId;

    private String firstName;

    private String lastName;

    private String specialty;

    private Long recordCount;
}
