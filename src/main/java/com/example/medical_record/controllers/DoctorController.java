package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.DTOs.doctor.DoctorRequestDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorWithPatientsDTO;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.ExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController
{
    private final DoctorService doctorService;

    private final ExaminationService examinationService;

    //GET ALL DOCTORS
    @GetMapping
    public String getDoctors(Model model)
    {
        List<DoctorResponseDTO> doctors = this.doctorService.getAllDoctors();

        model.addAttribute("doctors", doctors);

        return "doctor/doctors";
    }

    //GET SPECIFIC DOCTOR
    @GetMapping("/{id}")
    public DoctorResponseDTO getDoctorById(@PathVariable Long id)
    {
        return this.doctorService.getDoctorById(id);
    }

    //GET CREATE DOCTOR FORM
    @GetMapping("/create")
    public String getCreateDoctorPage(Model model)
    {
        model.addAttribute("doctor", new DoctorRequestDTO());

        return "doctor/create-doctor";
    }

    //SEND DOCTOR FORM
    @PostMapping("/create")
    public String createDoctor(@ModelAttribute("doctor") @Valid DoctorRequestDTO doctorToCreate, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "doctor/create-doctor";
        }

        this.doctorService.createDoctor(doctorToCreate);

        return "redirect:/doctors";
    }

    //GET EDIT DOCTOR FORM
    @GetMapping("/edit/{id}")
    public String getEditDoctorPage(@PathVariable Long id, Model model)
    {
        DoctorResponseDTO responseFromDB = this.doctorService.getDoctorById(id);

        model.addAttribute("doctor", responseFromDB);

        return "doctor/edit-doctor";
    }

    //SEND EDIT DOCTOR FORM
    @PostMapping("/edit/{id}")
    public String editDoctor(@PathVariable Long id, @ModelAttribute("doctor") @Valid DoctorRequestDTO doctorToEdit, BindingResult result) throws RuntimeException
    {
        if(result.hasErrors())
        {
            return "doctor/edit-doctor";
        }

        this.doctorService.updateDoctor(id, doctorToEdit);

        return "redirect:/doctors";
    }

    //DELETE DOCTOR
    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id)
    {
        this.doctorService.deleteDoctor(id);

        return "redirect:/doctors";
    }

    //GET ALL DOCTORS AND PATIENTS RELATED TO THEM
    @GetMapping("/with-patients")
    public String getDoctorsWithPatients(Model model)
    {
        List<DoctorWithPatientsDTO> doctorsWithPatients = this.doctorService.getAllDoctorsWithPatients();

        model.addAttribute("doctorsWithPatients", doctorsWithPatients);

        return "doctor/doctors-with-patients";
    }

    @GetMapping("/examination-counts")
    public String getDoctorExaminationCounts(Model model)
    {
        List<DoctorExaminationCountDTO> counts = this.examinationService.getExaminationCountsByDoctor();

        model.addAttribute("counts", counts);

        return "doctor/examination-counts";
    }
}
