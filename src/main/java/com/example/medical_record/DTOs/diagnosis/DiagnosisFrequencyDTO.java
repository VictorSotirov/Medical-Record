package com.example.medical_record.DTOs.diagnosis;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiagnosisFrequencyDTO
{
    private DiagnosisResponseDTO diagnosis;

    private Long frequency;
}
