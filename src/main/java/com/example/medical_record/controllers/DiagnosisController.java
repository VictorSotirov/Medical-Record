package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.diagnosis.DiagnosisFrequencyDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.services.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diagnoses")
public class DiagnosisController
{
    private final DiagnosisService diagnosisService;

    //GET ALL DIAGNOSES
    @GetMapping
    public String getDiagnoses(Model model)
    {
        List<DiagnosisResponseDTO> diagnoses = this.diagnosisService.getAllDiagnoses();

        model.addAttribute("diagnoses", diagnoses); // Add diagnoses to the model

        return "diagnosis/diagnoses"; // Return the correct Thymeleaf template
    }

    //GET SPECIFIC DIAGNOSIS
    @GetMapping("/{id}")
    public DiagnosisResponseDTO getDiagnosisById(@PathVariable Long id)
    {
        return this.diagnosisService.getDiagnosisById(id);
    }

    //GET CREATE DIAGNOSIS FORM
    @GetMapping("/create")
    public String getCreateDiagnosisPage(Model model)
    {
        model.addAttribute("diagnosis", new DiagnosisRequestDTO());

        return "diagnosis/create-diagnosis";
    }

    //SEND DIAGNOSIS FORM
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

    //GET EDIT DIAGNOSIS FORM
    @GetMapping("/edit/{id}")
    public String getEditDiagnosisPage(@PathVariable Long id, Model model)
    {
        DiagnosisResponseDTO responseFromDB = this.diagnosisService.getDiagnosisById(id);

        model.addAttribute("diagnosis", responseFromDB);

        return "diagnosis/edit-diagnosis";
    }

    //SEND EDIT DIAGNOSIS FORM
    @PostMapping("/edit/{id}")
    public String editDiagnosis(@PathVariable Long id, @ModelAttribute("diagnosis") DiagnosisRequestDTO diagnosisToEdit, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "diagnosis/edit-diagnosis";
        }

        this.diagnosisService.updateDiagnosis(id, diagnosisToEdit);

        return "redirect:/diagnoses"; // Redirect to the diagnoses list
    }

    //DELETE DIAGNOSIS
    @GetMapping("/delete/{id}")
    public String deleteDiagnosis(@PathVariable Long id)
    {
        this.diagnosisService.deleteDiagnosis(id);

        return "redirect:/diagnoses";
    }

    //GET MOST COMMON DIAGNOSES
    @GetMapping("/most-common")
    public String getMostCommonDiagnoses(Model model)
    {
        List<DiagnosisFrequencyDTO> mostCommonDiagnoses = diagnosisService.getMostCommonDiagnoses();

        model.addAttribute("mostCommonDiagnoses", mostCommonDiagnoses);

        return "diagnosis/most-common-diagnoses";
    }

}
