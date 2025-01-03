package com.example.medical_record.data;

import com.example.medical_record.data.entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long>
{
    //FIND MOST COMMON DIAGNOSES
    @Query("SELECT d, COUNT(e) FROM Diagnosis d " +
            "JOIN Examination e ON e.diagnosis.id = d.id " +
            "GROUP BY d " +
            "ORDER BY COUNT(e) DESC")
    List<Object[]> findMostCommonDiagnoses();
}
