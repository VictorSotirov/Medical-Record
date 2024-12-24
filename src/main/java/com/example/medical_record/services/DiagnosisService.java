package com.example.medical_record.services;

import com.example.medical_record.DTOs.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.DiagnosisResponseDTO;

import java.util.List;

public interface DiagnosisService
{
    void createDiagnosis(DiagnosisRequestDTO diagnosis);

    void updateDiagnosis(Long id, DiagnosisRequestDTO updatedDiagnosis);

    void deleteDiagnosis(Long id);

    DiagnosisResponseDTO getDiagnosisById(Long id);

    List<DiagnosisResponseDTO> getAllDiagnoses();
}
