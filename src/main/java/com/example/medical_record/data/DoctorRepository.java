package com.example.medical_record.data;

import com.example.medical_record.data.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>
{
    Optional<Doctor> findByIdAndIsDeletedFalse(Long id);

    List<Doctor> findByIsDeletedFalse();
}
