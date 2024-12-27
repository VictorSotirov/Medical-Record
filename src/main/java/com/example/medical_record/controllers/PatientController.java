package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.patient.PatientRequestDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController
{
    private final DoctorService doctorService;

    private final PatientService patientService;

    //GET ALL PATIENTS
    @GetMapping
    public String getPatients(Model model)
    {
        List<PatientResponseDTO> patients = this.patientService.getAllPatients();

        model.addAttribute("patients", patients);

        return "patient/patients";
    }

    //GET SPECIFIC PATIENT
    @GetMapping("/{id}")
    public PatientResponseDTO getPatientById(@PathVariable Long id)
    {
        return this.patientService.getPatientById(id);
    }

    //GET CREATE PATIENT FORM
    @GetMapping("/create")
    public String getCreatePatientPage(Model model)
    {
        model.addAttribute("patient", new PatientRequestDTO());

        model.addAttribute("doctors", this.doctorService.getAllDoctors());

        return "patient/create-patient";
    }

    //SEND PATIENT FORM
    @PostMapping("/create")
    public String createPatient(@ModelAttribute("patient") @Valid PatientRequestDTO patientToCreate, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "patient/create-patient";
        }

        this.patientService.createPatient(patientToCreate);

        return "redirect:/patients";
    }

    //GET EDIT PATIENT FORM
    @GetMapping("/edit/{id}")
    public String getEditPatientPage(@PathVariable Long id, Model model)
    {
        PatientResponseDTO responseFromDB = this.patientService.getPatientById(id);

        model.addAttribute("patient", responseFromDB);

        model.addAttribute("doctors", this.doctorService.getAllDoctors());

        return "patient/edit-patient";
    }

    //SEND EDIT PATIENT FORM
    @PostMapping("/edit/{id}")
    public String editPatient(@PathVariable Long id, @ModelAttribute("patient") @Valid PatientRequestDTO patientToEdit, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "patient/edit-patient";
        }

        this.patientService.updatePatient(id, patientToEdit);

        return "redirect:/patients";
    }

    //DELETE PATIENT
    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id)
    {
        this.patientService.deletePatient(id);

        return "redirect:/patients";
    }
}
