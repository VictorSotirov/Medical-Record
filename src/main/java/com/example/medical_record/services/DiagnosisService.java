package com.example.medical_record.services;

import com.example.medical_record.DTOs.diagnosis.*;
import com.example.medical_record.exceptions.diagnosis.DiagnosisAlreadyExistsException;
import com.example.medical_record.exceptions.diagnosis.DiagnosisNotFoundException;

import java.util.List;

public interface DiagnosisService
{
    void createDiagnosis(DiagnosisRequestDTO diagnosis) throws DiagnosisAlreadyExistsException;

    void updateDiagnosis(Long id, DiagnosisRequestDTO updatedDiagnosis) throws DiagnosisAlreadyExistsException, DiagnosisNotFoundException;

    void deleteDiagnosis(Long id) throws DiagnosisNotFoundException;

    DiagnosisResponseDTO getDiagnosisById(Long id) throws DiagnosisNotFoundException;

    List<DiagnosisResponseDTO> getAllDiagnoses();

    List<DiagnosisFrequencyDTO> getMostCommonDiagnoses();
}
