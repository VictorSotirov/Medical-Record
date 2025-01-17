package com.example.medical_record.services;

import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.DTOs.examination.ExaminationEditDTO;
import com.example.medical_record.DTOs.examination.ExaminationRequestDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExaminationService
{
    void createExamination(ExaminationRequestDTO examinationToCreate);

    void updateExamination(Long id, ExaminationEditDTO examinationToUpdate);

    void deleteExamination(Long id);

    ExaminationResponseDTO getExaminationById(Long id);

    List<ExaminationResponseDTO> getAllExaminations();

    List<DoctorExaminationCountDTO> getExaminationCountsByDoctor();

    List<ExaminationResponseDTO> getExaminationsByDoctorAndDate(Long doctorId, LocalDate startDate, LocalDate  endDate);

    List<ExaminationResponseDTO> getExamsForPatient(Long patientId);
}
