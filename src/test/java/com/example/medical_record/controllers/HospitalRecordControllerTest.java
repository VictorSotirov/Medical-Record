package com.example.medical_record.controllers;


import com.example.medical_record.DTOs.*;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorWithMostRecordsDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordEditDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordRequestDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.services.*;
import com.example.medical_record.services.impl.MonthWithHospitalRecordsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalRecordController.class)
@WithMockUser(roles="ADMIN") // or "DOCTOR", or test both separately
class HospitalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HospitalRecordService hospitalRecordService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private PatientService patientService;


    @Test
    @DisplayName("GET /hospital-records -> returns 'hospital-record/hospital-records' view with records")
    void getHospitalRecords_shouldReturnListView() throws Exception
    {
        List<HospitalRecordResponseDTO> mockRecords = List.of(
                new HospitalRecordResponseDTO(1L, LocalDate.of(2025,1,10), LocalDate.of(2025,1,15),
                        new PatientResponseDTO(), new DoctorResponseDTO()),
                new HospitalRecordResponseDTO(2L, LocalDate.of(2025,2,5), LocalDate.of(2025,2,10),
                        new PatientResponseDTO(), new DoctorResponseDTO())
        );

        when(hospitalRecordService.getAllHospitalRecords()).thenReturn(mockRecords);

        mockMvc.perform(get("/hospital-records"))
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/hospital-records"))
                .andExpect(model().attribute("hospitalRecords", mockRecords));
    }

    @Test
    @DisplayName("GET /hospital-records/create -> returns create-hospital-record view with form + patients/doctors")
    void getCreateHospitalRecordPage_shouldReturnCreateForm() throws Exception
    {
        when(patientService.getAllPatients()).thenReturn(List.of());
        when(doctorService.getAllDoctors()).thenReturn(List.of());

        mockMvc.perform(get("/hospital-records/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/create-hospital-record"))
                .andExpect(model().attributeExists("hospitalRecord"))
                .andExpect(model().attributeExists("patients"))
                .andExpect(model().attributeExists("doctors"));
    }
    @Test
    @DisplayName("POST /hospital-records/create -> redirects to /hospital-records on success")
    void createHospitalRecord_shouldRedirect_whenValid() throws Exception {
        doNothing().when(hospitalRecordService).createHospitalRecord(any(HospitalRecordRequestDTO.class));

        mockMvc.perform(post("/hospital-records/create")
                        .with(csrf())
                        // Provide valid params for the request
                        .param("admissionDate", "2025-01-10")
                        .param("dischargeDate", "2025-01-15")
                        .param("patientId", "10")
                        .param("doctorId", "2")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hospital-records"));
    }

    @Test
    @DisplayName("POST /hospital-records/create -> returns 'hospital-record/hospital-records' if validation fails")
    void createHospitalRecord_shouldReturnListView_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/hospital-records/create")
                        .with(csrf())
                        .param("admissionDate", "2025-01-10")
                        .param("dischargeDate", "") // blank => triggers error
                        .param("patientId", "10")
                        .param("doctorId", "2")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/hospital-records"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("hospitalRecord", "dischargeDate"));
    }
    @Test
    @DisplayName("GET /hospital-records/edit/{id} -> returns edit-hospital-record view with request+record+doctors")
    void getEditHospitalRecordPage_shouldReturnEditForm() throws Exception
    {
        HospitalRecordResponseDTO responseDto = new HospitalRecordResponseDTO(1L,
                LocalDate.of(2025,1,10), LocalDate.of(2025,1,15), null, null);
        when(hospitalRecordService.getHospitalRecordById(1L)).thenReturn(responseDto);
        when(doctorService.getAllDoctors()).thenReturn(List.of());

        mockMvc.perform(get("/hospital-records/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/edit-hospital-record"))
                .andExpect(model().attributeExists("hospitalRecordRequest"))
                .andExpect(model().attribute("hospitalRecord", responseDto))
                .andExpect(model().attributeExists("doctors"));
    }
    @Test
    @DisplayName("POST /hospital-records/edit/{id} -> redirects to /hospital-records when valid")
    void editHospitalRecord_shouldRedirect_whenValid() throws Exception
    {
        doNothing().when(hospitalRecordService).updateHospitalRecord(eq(1L), any(HospitalRecordEditDTO.class));

        mockMvc.perform(post("/hospital-records/edit/1")
                        .with(csrf())
                        .param("admissionDate", "2025-01-10")
                        .param("dischargeDate", "2025-01-15")
                        .param("doctorId", "2") // if these fields match your HospitalRecordEditDTO
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hospital-records"));
    }
    @Test
    @DisplayName("GET /hospital-records/delete/{id} -> redirects to /hospital-records")
    void deleteHospitalRecord_shouldRedirect() throws Exception {
        doNothing().when(hospitalRecordService).deleteHospitalRecord(1L);

        mockMvc.perform(get("/hospital-records/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hospital-records"));
    }

    @Test
    @DisplayName("GET /hospital-records/filter-by-date -> returns filter-hospital-records-by-timeframe")
    void filterHospitalRecordsByDate_shouldReturnTimeframeView() throws Exception
    {
        List<HospitalRecordResponseDTO> filteredRecords = List.of();
        when(hospitalRecordService.getHospitalRecordsByDateRange(
                LocalDate.of(2025,1,1), LocalDate.of(2025,12,31)))
                .thenReturn(filteredRecords);

        mockMvc.perform(get("/hospital-records/filter-by-date")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/filter-hospital-records-by-timeframe"))
                .andExpect(model().attribute("hospitalRecords", filteredRecords))
                .andExpect(model().attribute("startDate", "2025-01-01"))
                .andExpect(model().attribute("endDate", "2025-12-31"));
    }

    @Test
    @DisplayName("GET /hospital-records/doctor-with-most-records -> returns 'hospital-record/doctor-with-most-records'")
    void getDoctorWithMostRecords_shouldReturnViewAndModel() throws Exception
    {
        DoctorWithMostRecordsDTO mockDoctorRec = new DoctorWithMostRecordsDTO(2L,
                "Gregory", "House", "Diagnostics", 7L);

        when(hospitalRecordService.getDoctorWithMostRecords()).thenReturn(mockDoctorRec);

        mockMvc.perform(get("/hospital-records/doctor-with-most-records"))
                .andExpect(status().isOk())
                .andExpect(view().name("hospital-record/doctor-with-most-records"))
                .andExpect(model().attribute("doctorWithMostRecords", mockDoctorRec));
    }
}