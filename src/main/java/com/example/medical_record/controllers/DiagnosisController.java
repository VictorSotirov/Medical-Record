package com.example.medical_record.controllers;

import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.services.DiagnosisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@Controller
@RestController
@AllArgsConstructor
public class DiagnosisController
{
    private final DiagnosisService diagnosisService;

    @GetMapping("/diagnoses")
    public List<Diagnosis> getDiagnoses()
    {
        return diagnosisService.getAllDiagnoses();
    }


}
