package com.example.medical_record.services;

import com.example.medical_record.DTOs.examination.ExaminationEditDTO;
import com.example.medical_record.DTOs.examination.ExaminationRequestDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;

import java.util.List;

public interface ExaminationService
{
    void createExamination(ExaminationRequestDTO examinationToCreate);

    void updateExamination(Long id, ExaminationEditDTO examinationToUpdate);

    void deleteExamination(Long id);

    ExaminationResponseDTO getExaminationById(Long id);

    List<ExaminationResponseDTO> getAllExaminations();
}
