package com.example.medical_record.data;

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
}
