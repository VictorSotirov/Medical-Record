package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.DiagnosisResponseDTO;
import com.example.medical_record.services.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@Controller
@RequiredArgsConstructor
@RequestMapping("/diagnoses")
public class DiagnosisController
{
    private final DiagnosisService diagnosisService;

    @GetMapping
    public String getDiagnoses(Model model)
    {
        List<DiagnosisResponseDTO> diagnoses = diagnosisService.getAllDiagnoses();

        model.addAttribute("diagnoses", diagnoses); // Add diagnoses to the model

        return "diagnosis/diagnoses"; // Return the correct Thymeleaf template
    }

    @GetMapping("/{id}")
    public DiagnosisResponseDTO getDiagnosisById(@PathVariable Long id)
    {
        return this.diagnosisService.getDiagnosisById(id);
    }

    @GetMapping("/create")
    public String createDiagnosis(Model model)
    {
        model.addAttribute("diagnosis", new DiagnosisRequestDTO());

        return "diagnosis/create-diagnosis";
    }


    @PostMapping("/create")
    public String createDiagnosis(@ModelAttribute("diagnosis") DiagnosisRequestDTO diagnosisToCreate, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "diagnosis/create-diagnosis";
        }

        this.diagnosisService.createDiagnosis(diagnosisToCreate);

        return "redirect:/diagnoses";
    }





}
