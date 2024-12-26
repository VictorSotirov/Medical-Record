package com.example.medical_record.services;

import com.example.medical_record.DTOs.diagnosis.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;

import java.util.List;

public interface DiagnosisService
{
    void createDiagnosis(DiagnosisRequestDTO diagnosis);

    void updateDiagnosis(Long id, DiagnosisRequestDTO updatedDiagnosis);

    void deleteDiagnosis(Long id);

    DiagnosisResponseDTO getDiagnosisById(Long id);

    List<DiagnosisResponseDTO> getAllDiagnoses();
}
