package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorWithMostRecordsDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordEditDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordRequestDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordResponseDTO;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.HospitalRecordService;
import com.example.medical_record.services.PatientService;
import com.example.medical_record.services.impl.MonthWithHospitalRecordsDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
@RequestMapping("/hospital-records")
public class HospitalRecordController
{
    private final HospitalRecordService hospitalRecordService;

    private final DoctorService doctorService;

    private final PatientService patientService;

    //GET ALL HOSPITAL RECORDS
    @GetMapping
    public String getHospitalRecords(Model model)
    {
        List<HospitalRecordResponseDTO> hospitalRecords = hospitalRecordService.getAllHospitalRecords();

        model.addAttribute("hospitalRecords", hospitalRecords);

        return "hospital-record/hospital-records";
    }

    //GET CREATE HOSPITAL RECORD FORM
    @GetMapping("/create")
    public String getCreateHospitalRecordPage(Model model)
    {
        model.addAttribute("hospitalRecord", new HospitalRecordRequestDTO());

        model.addAttribute("patients", patientService.getAllPatients());

        model.addAttribute("doctors", doctorService.getAllDoctors());

        return "hospital-record/create-hospital-record";
    }

    @PostMapping("/create")
    public String createHospitalRecord(@ModelAttribute("hospitalRecord") @Valid HospitalRecordRequestDTO hospitalRecordToCreate, BindingResult result) throws  RuntimeException
    {
        if (result.hasErrors())
        {
            return "hospital-record/hospital-records";
        }

        this.hospitalRecordService.createHospitalRecord(hospitalRecordToCreate);

        return "redirect:/hospital-records";
    }


    //GET EDIT HOSPITAL RECORD FORM
    @GetMapping("/edit/{id}")
    public String getEditHospitalRecordPage(@PathVariable Long id, Model model)
    {
        HospitalRecordResponseDTO hospitalRecord = hospitalRecordService.getHospitalRecordById(id);

        HospitalRecordRequestDTO hospitalRecordRequest = new HospitalRecordRequestDTO();

        model.addAttribute("hospitalRecordRequest", hospitalRecordRequest);

        model.addAttribute("hospitalRecord", hospitalRecord);

        model.addAttribute("doctors", doctorService.getAllDoctors());

        return "hospital-record/edit-hospital-record";
    }

    //SEND EDIT HOSPITAL RECORD FORM
    //TODO HANDLE EXCEPTION PROPERLY WHEN THE DOCTOR ID IS NULL
    @PostMapping("/edit/{id}")
    public String editHospitalRecord(@PathVariable Long id, @ModelAttribute("hospitalRecord") @Valid HospitalRecordEditDTO hospitalRecordToEdit, BindingResult result) throws  RuntimeException
    {
        if (result.hasErrors())
        {
            return "hospital-record/edit-hospital-record";
        }

        this.hospitalRecordService.updateHospitalRecord(id, hospitalRecordToEdit);

        return "redirect:/hospital-records";
    }

    @GetMapping("/delete/{id}")
    public String deleteHospitalRecord(@PathVariable Long id)
    {
        this.hospitalRecordService.deleteHospitalRecord(id);

        return "redirect:/hospital-records";
    }

    //ADD CHECKS FOR THE END DATE NOT BEING BEFORE THE START DATE
    @GetMapping("/filter-by-date")
    public String filterHospitalRecordsByDate(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, Model model)
    {
        List<HospitalRecordResponseDTO> records = hospitalRecordService.getHospitalRecordsByDateRange(
                LocalDate.parse(startDate), LocalDate.parse(endDate));

        model.addAttribute("hospitalRecords", records);

        model.addAttribute("startDate", startDate);

        model.addAttribute("endDate", endDate);

        return "hospital-record/filter-hospital-records-by-timeframe";
    }

    @GetMapping("/most-records-month")
    public String getMonthWithMostHospitalRecords(Model model)
    {
        MonthWithHospitalRecordsDTO mostRecords = this.hospitalRecordService.getMonthWithMostHospitalRecords();

        model.addAttribute("mostRecordsMonth", Month.of(mostRecords.getMonth()).name());
        model.addAttribute("recordCount", mostRecords.getRecordCount());
        model.addAttribute("hospitalRecords", mostRecords.getRecords());

        return "hospital-record/most-records-month";
    }

    @GetMapping("/doctor-with-most-records")
    public String getDoctorWithMostRecords(Model model)
    {
        DoctorWithMostRecordsDTO doctorWithMostRecords = hospitalRecordService.getDoctorWithMostRecords();

        model.addAttribute("doctorWithMostRecords", doctorWithMostRecords);

        return "hospital-record/doctor-with-most-records";
    }

}
