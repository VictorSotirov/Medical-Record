package com.example.medical_record.services;

import com.example.medical_record.DTOs.patient.*;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.exceptions.patient.PatientNotFoundException;

import java.util.List;

public interface PatientService
{
    void createPatient(PatientRequestDTO patientToCreate);

    void updatePatient(Long id, PatientRequestDTO patientToUpdate) throws PatientNotFoundException, DoctorNotFoundException;

    void deletePatient(Long id) throws PatientNotFoundException;

    PatientResponseDTO getPatientById(Long id) throws PatientNotFoundException;

    List<PatientResponseDTO> getAllPatients();

    List<PatientResponseDTO> getAllPatientsWithSameDiagnosis(Long diagnosisId);

    List<PatientResponseDTO> getPatientsByDoctorId(Long doctorId);

    //GET ALL PATIENTS AND THEIR EXAMINATIONS
    List<PatientsWithExaminationsDTO> getAllPatientsWithExaminations();
}
