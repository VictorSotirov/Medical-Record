package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.diagnosis.DiagnosisFrequencyDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.exceptions.diagnosis.DiagnosisAlreadyExistsException;
import com.example.medical_record.exceptions.diagnosis.DiagnosisNotFoundException;
import com.example.medical_record.services.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public String getDiagnoses(Model model)
    {
        List<DiagnosisResponseDTO> diagnoses = this.diagnosisService.getAllDiagnoses();

        model.addAttribute("diagnoses", diagnoses);

        return "diagnosis/diagnoses";
    }

    //GET CREATE DIAGNOSIS FORM
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String getCreateDiagnosisPage(Model model)
    {
        model.addAttribute("diagnosis", new DiagnosisRequestDTO());

        return "diagnosis/create-diagnosis";
    }

    //SEND DIAGNOSIS FORM
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createDiagnosis(@ModelAttribute("diagnosis") @Valid DiagnosisRequestDTO diagnosisToCreate,
                                  BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            return "diagnosis/create-diagnosis";
        }

        try
        {
            this.diagnosisService.createDiagnosis(diagnosisToCreate);
        }
        catch (DiagnosisAlreadyExistsException e)
        {
            model.addAttribute("errorMessage", e.getMessage());

            return "diagnosis/create-diagnosis";
        }

        return "redirect:/diagnoses";
    }

    //GET EDIT DIAGNOSIS FORM
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getEditDiagnosisPage(@PathVariable Long id, Model model)
    {
        try
        {
            DiagnosisResponseDTO responseFromDB = this.diagnosisService.getDiagnosisById(id);

            model.addAttribute("diagnosis", responseFromDB);

            return "diagnosis/edit-diagnosis";
        }
        catch (DiagnosisNotFoundException dnfe)
        {
            model.addAttribute("errorMessage", dnfe.getMessage());

            return "diagnosis/diagnosis-not-found";
        }
    }

    //SEND EDIT DIAGNOSIS FORM
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editDiagnosis(@PathVariable Long id, @ModelAttribute("diagnosis") @Valid DiagnosisRequestDTO diagnosisToEdit, BindingResult result, Model model)
    {

        if(result.hasErrors())
        {
            model.addAttribute("diagnosis", diagnosisToEdit);

            model.addAttribute("id", id);

            return "diagnosis/edit-diagnosis";
        }

        try
        {
            this.diagnosisService.updateDiagnosis(id, diagnosisToEdit);
        }
        catch (DiagnosisAlreadyExistsException daee)
        {
            model.addAttribute("diagnosis", diagnosisToEdit);

            model.addAttribute("id", id);

            model.addAttribute("errorMessage", daee.getMessage());

            return "diagnosis/edit-diagnosis";
        }
        catch (DiagnosisNotFoundException dnfe)
        {
            model.addAttribute("diagnosis", diagnosisToEdit);

            model.addAttribute("id", id);

            model.addAttribute("errorMessage", dnfe.getMessage());

            return "diagnosis/edit-diagnosis";
        }

        return "redirect:/diagnoses"; // Redirect to the diagnoses list
    }

    //DELETE DIAGNOSIS
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDiagnosis(@PathVariable Long id, Model model)
    {
        try
        {
            this.diagnosisService.deleteDiagnosis(id);

            return "redirect:/diagnoses";
        }
        catch (DiagnosisNotFoundException dnfe)
        {
            model.addAttribute("errorMessage", dnfe.getMessage());

            return "diagnosis/diagnosis-not-found";
        }
    }

    //GET MOST COMMON DIAGNOSES
    @GetMapping("/most-common")
    @PreAuthorize("hasRole('ADMIN')")
    public String getMostCommonDiagnoses(Model model)
    {
        List<DiagnosisFrequencyDTO> mostCommonDiagnoses = diagnosisService.getMostCommonDiagnoses();

        model.addAttribute("mostCommonDiagnoses", mostCommonDiagnoses);

        return "diagnosis/most-common-diagnoses";
    }

}
