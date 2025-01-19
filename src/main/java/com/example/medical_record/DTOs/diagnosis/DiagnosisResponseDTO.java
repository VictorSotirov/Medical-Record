package com.example.medical_record.DTOs.diagnosis;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisResponseDTO
{
    private Long id;

    private String description;
}
