package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import com.example.medical_record.DTOs.patient.PatientRequestDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.DTOs.patient.PatientsWithExaminationsDTO;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.entities.auth.User;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.exceptions.patient.PatientNotFoundException;
import com.example.medical_record.services.DiagnosisService;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.ExaminationService;
import com.example.medical_record.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController
{
    private final DoctorService doctorService;

    private final PatientService patientService;

    private final DiagnosisService diagnosisService;

    //GET ALL PATIENTS
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getPatients(Model model)
    {
        List<PatientResponseDTO> patients = this.patientService.getAllPatients();

        List<DiagnosisResponseDTO> diagnoses = this.diagnosisService.getAllDiagnoses();

        List<DoctorResponseDTO> doctors = this.doctorService.getAllDoctors();

        model.addAttribute("patients", patients);

        model.addAttribute("diagnoses", diagnoses);

        model.addAttribute("doctors", doctors);

        return "patient/patients";
    }

    //GET CREATE PATIENT FORM
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String getCreatePatientPage(Model model)
    {
        model.addAttribute("patient", new PatientRequestDTO());

        model.addAttribute("doctors", this.doctorService.getAllDoctors());

        return "patient/create-patient";
    }

    //SEND PATIENT FORM
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public String getEditPatientPage(@PathVariable Long id, Model model)
    {
        try
        {
            PatientResponseDTO responseFromDB = this.patientService.getPatientById(id);

            PatientRequestDTO editDTO = new PatientRequestDTO();

            editDTO.setFirstName(responseFromDB.getFirstName());

            editDTO.setLastName(responseFromDB.getLastName());

            editDTO.setHealthInsurancePaid(responseFromDB.isHealthInsurancePaid());

            if (responseFromDB.getPersonalDoctor() != null)
            {
                editDTO.setPersonalDoctorId(responseFromDB.getPersonalDoctor().getId());
            }

            model.addAttribute("patient", editDTO);

            model.addAttribute("patientId", id);

            model.addAttribute("doctors", this.doctorService.getAllDoctors());

            return "patient/edit-patient";
        }
        catch (PatientNotFoundException pnfe)
        {
            model.addAttribute("errorMessage", pnfe.getMessage());

            return "patient/patient-not-found";
        }
    }

    //SEND EDIT PATIENT FORM
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editPatient(@PathVariable Long id, @ModelAttribute("patient") @Valid PatientRequestDTO patientToEdit, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            return "patient/edit-patient";
        }

        try
        {
            this.patientService.updatePatient(id, patientToEdit);

            return "redirect:/patients";
        }
        catch (PatientNotFoundException pnfe)
        {
            model.addAttribute("errorMessage", pnfe.getMessage());

            return "patient/patient-not-found";
        }
        catch (DoctorNotFoundException dnfe)
        {
            model.addAttribute("errorMessage", dnfe.getMessage());

            return "patient/patient-not-found";
        }
    }

    //DELETE PATIENT
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePatient(@PathVariable Long id, Model model)
    {
        try
        {
            this.patientService.deletePatient(id);

            return "redirect:/patients";
        }
        catch (PatientNotFoundException pnfe)
        {
            model.addAttribute("errorMessage", pnfe.getMessage());

            return "patient/patient-not-found";
        }
    }


    //GET ALL PATIENTS WITH SAME DIAGNOSIS
    @GetMapping("/filter-by-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String filterPatientsByDiagnosis(@RequestParam(required = false) Long diagnosisId, Model model)
    {

        List<PatientResponseDTO> patients = this.patientService.getAllPatientsWithSameDiagnosis(diagnosisId);

        List<DiagnosisResponseDTO> diagnoses = this.diagnosisService.getAllDiagnoses();

        model.addAttribute("patients", patients);

        model.addAttribute("diagnoses", diagnoses); // Ensure diagnoses are loaded after filtering

        model.addAttribute("selectedDiagnosisId", diagnosisId); // To maintain selected filter in the dropdown

        return "patient/patients";
    }

    //GET ALL PATIENTS WITH SAME DOCTOR
    @GetMapping("/filter-by-doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String filterPatientsByDoctor(@RequestParam(required = false) Long doctorId, Model model)
    {
        List<PatientResponseDTO> patients = this.patientService.getPatientsByDoctorId(doctorId);

        List<DiagnosisResponseDTO> diagnoses = this.diagnosisService.getAllDiagnoses();

        List<DoctorResponseDTO> doctors = this.doctorService.getAllDoctors();

        model.addAttribute("patients", patients);

        model.addAttribute("diagnoses", diagnoses); // Keep diagnoses for other filter

        model.addAttribute("doctors", doctors);

        model.addAttribute("selectedDoctorId", doctorId); // Keep selected doctor in dropdown

        return "patient/patients";
    }

    @GetMapping("/with-examinations")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String getPatientsWithExaminations(Model model)
    {
        List<PatientsWithExaminationsDTO> patientsWithExaminations = patientService.getAllPatientsWithExaminations();

        model.addAttribute("patientsWithExaminations", patientsWithExaminations);

        return "patient/patients-with-examinations";
    }
}
