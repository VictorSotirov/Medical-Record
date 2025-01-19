package com.example.medical_record.DTOs.doctor;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTO
{
    private Long id;

    private String firstName;

    private String lastName;

    private String specialty;
}
