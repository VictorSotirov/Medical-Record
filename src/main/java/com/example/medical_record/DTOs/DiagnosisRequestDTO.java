package com.example.medical_record.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisRequestDTO
{
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
