package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>
{
    Optional<Doctor> findByIdAndIsDeletedFalse(Long id);

    List<Doctor> findByIsDeletedFalse();
}
