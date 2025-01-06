package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.examination.ExaminationEditDTO;
import com.example.medical_record.DTOs.examination.ExaminationRequestDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import com.example.medical_record.services.DiagnosisService;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.ExaminationService;
import com.example.medical_record.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/examinations")
public class ExaminationController
{
    private final ExaminationService examinationService;

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final DiagnosisService diagnosisService;

    //GET ALL EXAMINATIONS
    @GetMapping
    public String getExaminations(Model model)
    {
        List<ExaminationResponseDTO> examinations = this.examinationService.getAllExaminations();

        List<DoctorResponseDTO> doctors = this.doctorService.getAllDoctors();

        model.addAttribute("examinations", examinations);

        model.addAttribute("doctors", doctors);

        return "examination/examinations";
    }

    //GET SPECIFIC EXAMINATIONS
    @GetMapping("/{id}")
    public ExaminationResponseDTO getExaminationById(@PathVariable Long id)
    {
        return this.examinationService.getExaminationById(id);
    }

    @GetMapping("/create")
    public String getCreateExaminationPage(Model model)
    {
        model.addAttribute("examination", new ExaminationRequestDTO());

        model.addAttribute("patients", patientService.getAllPatients());

        model.addAttribute("doctors", doctorService.getAllDoctors());

        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());

        return "examination/create-examination";
    }

    // SEND CREATE EXAMINATION FORM
    @PostMapping("/create")
    public String createExamination(@ModelAttribute("examination") @Valid ExaminationRequestDTO examinationToCreate, BindingResult result)
    {
        if (result.hasErrors())
        {
            return "examination/create-examination";
        }

        examinationService.createExamination(examinationToCreate);

        return "redirect:/examinations";
    }

    // GET EDIT EXAMINATION FORM
    @GetMapping("/edit/{id}")
    public String getEditExaminationPage(@PathVariable Long id, Model model)
    {
        ExaminationResponseDTO examination = examinationService.getExaminationById(id);

        ExaminationRequestDTO examinationRequest = new ExaminationRequestDTO();

        model.addAttribute("examination", examination);

        model.addAttribute("examinationRequest", examinationRequest);

        model.addAttribute("patients", patientService.getAllPatients());

        model.addAttribute("doctors", doctorService.getAllDoctors());

        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());

        return "examination/edit-examination";
    }

    // POST EDIT EXAMINATION FORM
    @PostMapping("/edit/{id}")
    public String editExamination(@PathVariable Long id, @ModelAttribute("examination") @Valid ExaminationEditDTO examinationToEdit, BindingResult result)
    {
        if (result.hasErrors())
        {
            return "examination/edit-examination";
        }

        examinationService.updateExamination(id, examinationToEdit);

        return "redirect:/examinations";
    }

    // DELETE EXAMINATION
    @GetMapping("/delete/{id}")
    public String deleteExamination(@PathVariable Long id)
    {
        this.examinationService.deleteExamination(id);
        return "redirect:/examinations";
    }

    @GetMapping("/filter-by-doctor-and-date")
    public String filterExaminationsByDoctorAndDate(@RequestParam("doctorId") Long doctorId,
                                                    @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate, Model model)
    {
        List<ExaminationResponseDTO> examinations = examinationService.getExaminationsByDoctorAndDate(
                doctorId, LocalDate.parse(startDate), LocalDate.parse(endDate));

        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();

        model.addAttribute("examinations", examinations);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("doctors", doctors);

        return "examination/filter-examinations-by-doctor-and-date";
    }


}
