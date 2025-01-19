package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import com.example.medical_record.DTOs.patient.*;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.exceptions.patient.PatientNotFoundException;
import com.example.medical_record.services.DiagnosisService;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@WithMockUser(roles = "ADMIN")  // Ensures user is logged in as ADMIN
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private DiagnosisService diagnosisService;


    @Test
    @DisplayName("GET /patients -> returns 'patient/patients' view with patients, diagnoses, and doctors in model")
    void getPatients_shouldReturnPatientListViewAndModel() throws Exception
    {
        List<PatientResponseDTO> mockPatients = List.of(
                new PatientResponseDTO(1L, "John", "Doe", true, null),
                new PatientResponseDTO(2L, "Jane", "Smith", false, null));

        List<DiagnosisResponseDTO> mockDiagnoses = List.of(
                new DiagnosisResponseDTO(10L, "Flu"),
                new DiagnosisResponseDTO(11L, "Cold")
        );
        List<DoctorResponseDTO> mockDoctors = List.of(
                new DoctorResponseDTO(100L, "Dr. House", "MD", "Diagnostics"),
                new DoctorResponseDTO(101L, "Dr. Wilson", "James", "Oncology")
        );

        when(patientService.getAllPatients()).thenReturn(mockPatients);
        when(diagnosisService.getAllDiagnoses()).thenReturn(mockDiagnoses);
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients"))
                .andExpect(model().attribute("patients", mockPatients))
                .andExpect(model().attribute("diagnoses", mockDiagnoses))
                .andExpect(model().attribute("doctors", mockDoctors));
    }

    @Test
    @DisplayName("GET /patients/create -> returns 'patient/create-patient' view with an empty form DTO")
    void getCreatePatientPage_shouldReturnCreateForm() throws Exception
    {
        List<DoctorResponseDTO> mockDoctors = List.of(
                new DoctorResponseDTO(101L, "Dr. Wilson", "James", "Oncology")
        );
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/patients/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/create-patient"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("doctors", mockDoctors));
    }

    @Test
    @DisplayName("POST /patients/create -> redirects to /patients on success")
    void createPatient_shouldRedirect_whenValid() throws Exception
    {
        doNothing().when(patientService).createPatient(any(PatientRequestDTO.class));

        mockMvc.perform(post("/patients/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("healthInsurancePaid", "true")
                        .with(csrf())  // CSRF required for POST
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).createPatient(any(PatientRequestDTO.class));
    }

    @Test
    @DisplayName("POST /patients/create -> returns form if validation fails (example: blank fields)")
    void createPatient_shouldReturnForm_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/patients/create")
                        // Missing or blank param => triggers validation error
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("healthInsurancePaid", "true")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/create-patient"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("patient", "firstName", "lastName"));
    }

    @Test
    @DisplayName("GET /patients/edit/{id} -> returns edit form when patient found")
    void getEditPatientPage_shouldReturnEditForm_whenPatientExists() throws Exception
    {
        PatientResponseDTO existingPatient = new PatientResponseDTO(1L, "John", "Doe", true, null);

        when(patientService.getPatientById(1L)).thenReturn(existingPatient);

        List<DoctorResponseDTO> mockDoctors = List.of(
                new DoctorResponseDTO(101L, "Dr. Wilson", "James", "Oncology")
        );
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/edit-patient"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patientId", 1L))
                .andExpect(model().attribute("doctors", mockDoctors));
    }

    @Test
    @DisplayName("GET /patients/edit/{id} -> returns patient-not-found if not found")
    void getEditPatientPage_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception
    {
        when(patientService.getPatientById(999L))
                .thenThrow(new PatientNotFoundException("Patient with id 999 not found"));

        mockMvc.perform(get("/patients/edit/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attribute("errorMessage", "Patient with id 999 not found"));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> redirects to /patients on success")
    void editPatient_shouldRedirect_whenValid() throws Exception
    {
        doNothing().when(patientService).updatePatient(eq(1L), any(PatientRequestDTO.class));

        mockMvc.perform(post("/patients/edit/1")
                        .with(csrf())
                        .param("firstName", "UpdatedName")
                        .param("lastName", "UpdatedLast")
                        .param("healthInsurancePaid", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> returns 'patient-not-found' if patient not found")
    void editPatient_shouldReturnNotFound_whenPatientNotExists() throws Exception
    {
        doThrow(new PatientNotFoundException("Not found"))
                .when(patientService).updatePatient(eq(1L), any(PatientRequestDTO.class));

        mockMvc.perform(post("/patients/edit/1")
                        .with(csrf())
                        .param("firstName", "Something")
                        .param("lastName", "Else")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attribute("errorMessage", "Not found"));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> returns 'patient-not-found' if doctor not found")
    void editPatient_shouldReturnNotFound_whenDoctorNotExists() throws Exception
    {
        doThrow(new DoctorNotFoundException("Doctor not found"))
                .when(patientService).updatePatient(eq(1L), any(PatientRequestDTO.class));

        mockMvc.perform(post("/patients/edit/1")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("healthInsurancePaid", "true")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attribute("errorMessage", "Doctor not found"));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> returns edit form if validation fails")
    void editPatient_shouldReturnEditForm_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/patients/edit/1")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "Doe")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/edit-patient"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("patient", "firstName"));
    }

    @Test
    @DisplayName("GET /patients/delete/{id} -> redirect to /patients on success")
    void deletePatient_shouldRedirect_whenFound() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(get("/patients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @DisplayName("GET /patients/delete/{id} -> return patient-not-found if not found")
    void deletePatient_shouldReturnNotFound_whenMissing() throws Exception
    {
        doThrow(new PatientNotFoundException("Patient not found"))
                .when(patientService).deletePatient(999L);

        mockMvc.perform(get("/patients/delete/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attribute("errorMessage", "Patient not found"));
    }

    @Test
    @DisplayName("GET /patients/filter-by-diagnosis?diagnosisId=X -> returns patients with that diagnosis & diagnoses in model")
    void filterPatientsByDiagnosis_shouldReturnPatients_andDiagnoses() throws Exception {
        Long diagnosisId = 10L;
        List<PatientResponseDTO> filteredPatients = List.of(
                new PatientResponseDTO(1L, "John", "Doe", true, null)
        );
        List<DiagnosisResponseDTO> allDiagnoses = List.of(
                new DiagnosisResponseDTO(10L, "Flu"), new DiagnosisResponseDTO(11L, "Cold")
        );

        when(patientService.getAllPatientsWithSameDiagnosis(diagnosisId))
                .thenReturn(filteredPatients);
        when(diagnosisService.getAllDiagnoses()).thenReturn(allDiagnoses);

        mockMvc.perform(get("/patients/filter-by-diagnosis")
                        .param("diagnosisId", diagnosisId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients"))
                .andExpect(model().attribute("patients", filteredPatients))
                .andExpect(model().attribute("diagnoses", allDiagnoses))
                .andExpect(model().attribute("selectedDiagnosisId", diagnosisId));
    }

    @Test
    @DisplayName("GET /patients/filter-by-doctor?doctorId=X -> returns filtered patients & doctors, diagnoses in model")
    void filterPatientsByDoctor_shouldReturnPatients_andDoctorsAndDiagnoses() throws Exception {
        Long doctorId = 100L;
        List<PatientResponseDTO> filteredPatients = List.of(
                new PatientResponseDTO(1L, "Mary", "Smith", false, null)
        );
        List<DiagnosisResponseDTO> mockDiagnoses = List.of(
                new DiagnosisResponseDTO(10L, "Flu"), new DiagnosisResponseDTO(11L, "Cold")
        );
        List<DoctorResponseDTO> mockDoctors = List.of(
                new DoctorResponseDTO(100L, "Dr. House", "MD", "Diagnostics"),
                new DoctorResponseDTO(101L, "Dr. Wilson", "James", "Oncology")
        );

        when(patientService.getPatientsByDoctorId(doctorId)).thenReturn(filteredPatients);
        when(diagnosisService.getAllDiagnoses()).thenReturn(mockDiagnoses);
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/patients/filter-by-doctor")
                        .param("doctorId", doctorId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients"))
                .andExpect(model().attribute("patients", filteredPatients))
                .andExpect(model().attribute("diagnoses", mockDiagnoses))
                .andExpect(model().attribute("doctors", mockDoctors))
                .andExpect(model().attribute("selectedDoctorId", doctorId));
    }

    @Test
    @DisplayName("GET /patients/with-examinations -> returns 'patient/patients-with-examinations' view")
    void getPatientsWithExaminations_shouldReturnViewAndModel() throws Exception {
        List<PatientsWithExaminationsDTO> mockList = List.of(
                new PatientsWithExaminationsDTO(new PatientResponseDTO(1L, "John", "Doe", true, new DoctorResponseDTO(1L, "Petar", "Petrov", "Cardiology")), List.of(
                        new ExaminationResponseDTO(101L, null, "Desc1", 2, null, null, null, null)
                ))
        );
        when(patientService.getAllPatientsWithExaminations()).thenReturn(mockList);

        mockMvc.perform(get("/patients/with-examinations"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients-with-examinations"))
                .andExpect(model().attribute("patientsWithExaminations", mockList));
    }
}