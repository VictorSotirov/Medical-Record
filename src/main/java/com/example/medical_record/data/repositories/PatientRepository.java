package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>
{
    Optional<Patient> findByIdAndIsDeletedFalse(Long id);

    List<Patient> findByIsDeletedFalse();

    //FIND ALL PATIENT BY SPECIFIC DIAGNOSIS
    List<Patient> findDistinctByExaminationsDiagnosisIdAndIsDeletedFalse(Long diagnosisId);

    //FIND ALL PATIENTS BY SPECIFIC DOCTOR
    List<Patient> findByPersonalDoctorIdAndIsDeletedFalse(Long doctorId);
}
