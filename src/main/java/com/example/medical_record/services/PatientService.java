package com.example.medical_record.services;

import com.example.medical_record.DTOs.patient.*;

import java.util.List;

public interface PatientService
{
    void createPatient(PatientRequestDTO patientToCreate);

    void updatePatient(Long id, PatientRequestDTO patientToUpdate);

    void deletePatient(Long id);

    PatientResponseDTO getPatientById(Long id);

    List<PatientResponseDTO> getAllPatients();

    List<PatientResponseDTO> getAllPatientsWithSameDiagnosis(Long diagnosisId);

    List<PatientResponseDTO> getPatientsByDoctorId(Long doctorId);
}
