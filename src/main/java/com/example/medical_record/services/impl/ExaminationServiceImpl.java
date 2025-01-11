package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.examination.ExaminationEditDTO;
import com.example.medical_record.DTOs.examination.ExaminationRequestDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.ExaminationRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import com.example.medical_record.data.entities.*;
import com.example.medical_record.services.ExaminationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExaminationServiceImpl implements ExaminationService
{
    private final ExaminationRepository examinationRepository;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final DiagnosisRepository diagnosisRepository;

    //CREATE EXAMINATION RECORD
    @Override
    public void createExamination(ExaminationRequestDTO examinationToCreate)
    {
        Examination examination = mapToEntity(examinationToCreate);

        this.examinationRepository.save(examination);
    }

    //UPDATE EXAMINATION RECORD
    @Override
    public void updateExamination(Long id, ExaminationEditDTO examinationToUpdate)
    {
        Examination existingExamination = this.examinationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Examination with id " + id + " not found."));

        existingExamination.setExaminationDate(examinationToUpdate.getExaminationDate());

        existingExamination.setTreatmentDescription(examinationToUpdate.getTreatmentDescription());

        existingExamination.setSickLeaveDays(examinationToUpdate.getSickLeaveDays());

        existingExamination.setSickLeaveStartDate(examinationToUpdate.getSickLeaveStartDate());

        // Update doctor if provided
        if (examinationToUpdate.getDoctorId() != null)
        {
            Doctor doctor = this.doctorRepository.findByIdAndIsDeletedFalse(examinationToUpdate.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor with id " + examinationToUpdate.getDoctorId() + " not found."));

            existingExamination.setDoctor(doctor);
        }

        // Update diagnosis if provided
        if (examinationToUpdate.getDiagnosisId() != null)
        {
            Diagnosis diagnosis = this.diagnosisRepository.findById(examinationToUpdate.getDiagnosisId())
                    .orElseThrow(() -> new RuntimeException("Diagnosis with id " + examinationToUpdate.getDiagnosisId() + " not found."));
            existingExamination.setDiagnosis(diagnosis);
        }

        this.examinationRepository.save(existingExamination);
    }

    //DELETE EXAMINATION RECORD
    @Override
    public void deleteExamination(Long id)
    {
        Examination examination = this.examinationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Examination with id " + id + " not found."));

        examination.setDeleted(true);

        this.examinationRepository.save(examination);
    }

    //GET EXAMINATION BY ID
    @Override
    public ExaminationResponseDTO getExaminationById(Long id)
    {

        Examination examination = this.examinationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Examination with id " + id + " not found."));

        return mapToResponseDTO(examination);
    }

    //GET ALL EXAMINATIONS
    @Override
    public List<ExaminationResponseDTO> getAllExaminations()
    {
        List<Examination> activeRecords = this.examinationRepository.findByIsDeletedFalse();

        return activeRecords.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorExaminationCountDTO> getExaminationCountsByDoctor()
    {
        return this.examinationRepository.getExaminationCountsByDoctor();
    }

    @Override
    public List<ExaminationResponseDTO> getExaminationsByDoctorAndDate(Long doctorId, LocalDate startDate, LocalDate  endDate)
    {
        List<Examination> examinations = this.examinationRepository.findByDoctorIdAndExaminationDateBetweenAndIsDeletedFalse(doctorId, startDate, endDate);

        return examinations.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // Helper method to map entity to DTO
    private ExaminationResponseDTO mapToResponseDTO(Examination examination)
    {
        ExaminationResponseDTO responseDTO = new ExaminationResponseDTO();

        responseDTO.setId(examination.getId());

        responseDTO.setExaminationDate(examination.getExaminationDate());

        responseDTO.setTreatmentDescription(examination.getTreatmentDescription());

        responseDTO.setSickLeaveDays(examination.getSickLeaveDays());

        responseDTO.setSickLeaveStartDate(examination.getSickLeaveStartDate());

        // Map patient to PatientResponseDTO
        PatientResponseDTO patientDTO = new PatientResponseDTO();

        patientDTO.setId(examination.getPatient().getId());

        patientDTO.setFirstName(examination.getPatient().getFirstName());

        patientDTO.setLastName(examination.getPatient().getLastName());

        responseDTO.setPatient(patientDTO);

        // Map doctor to DoctorResponseDTO
        DoctorResponseDTO doctorDTO = new DoctorResponseDTO();

        doctorDTO.setId(examination.getDoctor().getId());

        doctorDTO.setFirstName(examination.getDoctor().getFirstName());

        doctorDTO.setLastName(examination.getDoctor().getLastName());

        doctorDTO.setSpecialty(examination.getDoctor().getSpecialty());

        responseDTO.setDoctor(doctorDTO);

        // Map diagnosis to DiagnosisResponseDTO
        DiagnosisResponseDTO diagnosisDTO = new DiagnosisResponseDTO();

        diagnosisDTO.setId(examination.getDiagnosis().getId());

        diagnosisDTO.setDescription(examination.getDiagnosis().getDescription());

        responseDTO.setDiagnosis(diagnosisDTO);

        return responseDTO;
    }

    // Helper method to map DTO to entity
    private Examination mapToEntity(ExaminationRequestDTO examinationRequestDTO)
    {
        Examination examination = new Examination();

        examination.setExaminationDate(examinationRequestDTO.getExaminationDate());

        examination.setTreatmentDescription(examinationRequestDTO.getTreatmentDescription());

        examination.setSickLeaveDays(examinationRequestDTO.getSickLeaveDays());

        examination.setSickLeaveStartDate(examinationRequestDTO.getSickLeaveStartDate());

        // Map patient ID to Patient entity
        Patient patient = this.patientRepository.findById(examinationRequestDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient with id " + examinationRequestDTO.getPatientId() + " not found."));
        examination.setPatient(patient);

        // Map doctor ID to Doctor entity
        Doctor doctor = this.doctorRepository.findById(examinationRequestDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor with id " + examinationRequestDTO.getDoctorId() + " not found."));
        examination.setDoctor(doctor);

        // Map diagnosis ID to Diagnosis entity
        Diagnosis diagnosis = this.diagnosisRepository.findById(examinationRequestDTO.getDiagnosisId())
                .orElseThrow(() -> new RuntimeException("Diagnosis with id " + examinationRequestDTO.getDiagnosisId() + " not found."));
        examination.setDiagnosis(diagnosis);

        return examination;
    }
}
