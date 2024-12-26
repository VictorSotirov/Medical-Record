package com.example.medical_record.data;

import com.example.medical_record.data.entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long>
{
}
