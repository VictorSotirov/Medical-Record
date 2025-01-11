package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.HospitalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    //FIND MONTH WITH MOIST HOSPITAL RECORDS
    @Query("SELECT MONTH(hr.admissionDate) AS month, COUNT(hr) AS recordCount " +
            "FROM HospitalRecord hr " +
            "WHERE YEAR(hr.admissionDate) = YEAR(CURRENT_DATE) AND hr.isDeleted = false " +
            "GROUP BY MONTH(hr.admissionDate) " +
            "ORDER BY COUNT(hr) DESC")
    List<Object[]> findMonthWithMostHospitalRecords();

    //FIND RECORDS OF MONTH WITH MOIST HOSPITAL RECORDS
    @Query("SELECT hr FROM HospitalRecord hr " +
            "WHERE YEAR(hr.admissionDate) = YEAR(CURRENT_DATE) AND MONTH(hr.admissionDate) = :month " +
            "AND hr.isDeleted = false")
    List<HospitalRecord> findRecordsByMonth(int month);

    @Query("SELECT d.id, d.firstName, d.lastName, d.specialty, COUNT(hr) AS recordCount " +
            "FROM HospitalRecord hr JOIN hr.doctor d " +
            "WHERE hr.isDeleted = false " +
            "GROUP BY d.id, d.firstName, d.lastName, d.specialty " +
            "ORDER BY recordCount DESC")
    List<Object[]> findDoctorWithMostRecords();
}
