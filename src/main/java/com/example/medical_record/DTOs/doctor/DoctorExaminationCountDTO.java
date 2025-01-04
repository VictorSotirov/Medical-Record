package com.example.medical_record.DTOs.doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorExaminationCountDTO
{
    private Long doctorId;

    private String firstName;

    private String lastName;

    private String specialty;

    private Long examinationCount;
}
