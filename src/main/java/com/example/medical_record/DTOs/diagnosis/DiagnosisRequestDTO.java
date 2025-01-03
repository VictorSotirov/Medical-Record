package com.example.medical_record.DTOs.diagnosis;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisRequestDTO
{
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
