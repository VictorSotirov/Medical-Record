package com.example.medical_record.services;

import com.example.medical_record.data.entities.Diagnosis;

import java.util.List;

public interface DiagnosisService
{
    List<Diagnosis> getAllDiagnoses();
}
