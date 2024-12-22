package com.example.medical_record.services.impl;

import com.example.medical_record.data.DiagnosisRepository;
import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.services.DiagnosisService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService
{
    private final DiagnosisRepository diagnosisRepository;

    @Override
    public List<Diagnosis> getAllDiagnoses()
    {
        return null;
    }
}
