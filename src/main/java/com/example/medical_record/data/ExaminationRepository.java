package com.example.medical_record.data;

import com.example.medical_record.data.entities.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long>
{
    Optional<Examination> findByIdAndIsDeletedFalse(Long id);

    List<Examination> findByIsDeletedFalse();
}
