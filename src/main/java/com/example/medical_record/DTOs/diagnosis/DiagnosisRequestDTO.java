package com.example.medical_record.DTOs.diagnosis;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisRequestDTO
{
    @NotBlank(message = "Description cannot be blank!")
    private String description;
}
