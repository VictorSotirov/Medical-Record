package com.example.medical_record.data;

import com.example.medical_record.data.entities.HospitalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRecordRepository extends JpaRepository<HospitalRecord, Long>
{
    Optional<HospitalRecord> findByIdAndIsDeletedFalse(Long id);

    List<HospitalRecord> findByIsDeletedFalse();

    List<HospitalRecord> findByAdmissionDateBetweenAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);
}
