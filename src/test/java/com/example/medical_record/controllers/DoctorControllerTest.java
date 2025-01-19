package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.RoleDTO;
import com.example.medical_record.DTOs.UserDTO;
import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.DTOs.doctor.DoctorRequestDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorWithPatientsDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.services.DoctorService;
import com.example.medical_record.services.ExaminationService;
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

@WebMvcTest(DoctorController.class)
@WithMockUser(roles = "ADMIN") // or DOCTOR; or test both separately
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private ExaminationService examinationService;

    @Test
    @DisplayName("GET /doctors -> returns 'doctor/doctors' with list of doctors")
    void getDoctors_shouldReturnDoctorsViewAndModel() throws Exception
    {
        List<DoctorResponseDTO> mockDoctors = List.of(
                new DoctorResponseDTO(1L, "Dr. House", "G", "Diagnostics"),
                new DoctorResponseDTO(2L, "Dr. Wilson", "James", "Oncology")
        );
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctors"))
                .andExpect(model().attribute("doctors", mockDoctors));
    }

    @Test
    @DisplayName("GET /doctors/create -> returns 'doctor/create-doctor' with empty form")
    void getCreateDoctorPage_shouldReturnCreateForm() throws Exception {
        mockMvc.perform(get("/doctors/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/create-doctor"))
                .andExpect(model().attributeExists("doctor"));
    }

    @Test
    @DisplayName("POST /doctors/create -> redirects to /doctors on success")
    void createDoctor_shouldRedirect_whenValid() throws Exception
    {
        doNothing().when(doctorService).createDoctor(any(DoctorRequestDTO.class));

        mockMvc.perform(post("/doctors/create")
                        .with(csrf())
                        // if your DoctorRequestDTO has e.g. firstName, lastName, specialty
                        .param("firstName", "Gregory")
                        .param("lastName", "House")
                        .param("specialty", "Diagnostics")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));

        verify(doctorService).createDoctor(any(DoctorRequestDTO.class));
    }

    @Test
    @DisplayName("POST /doctors/create -> returns form if validation fails")
    void createDoctor_shouldReturnForm_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/doctors/create")
                        .with(csrf())
                        .param("firstName", "") // blank
                        .param("lastName", "SomeLast")
                        .param("specialty", "Anything")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/create-doctor"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("doctor", "firstName"));
    }

    @Test
    @DisplayName("GET /doctors/edit/{id} -> returns edit-doctor view when found")
    void getEditDoctorPage_shouldReturnEditView_whenDoctorExists() throws Exception {
        DoctorResponseDTO mockDoctor = new DoctorResponseDTO(1L, "Dr. House", "G", "Diagnostics");
        when(doctorService.getDoctorById(1L)).thenReturn(mockDoctor);

        mockMvc.perform(get("/doctors/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/edit-doctor"))
                .andExpect(model().attribute("doctor", mockDoctor));
    }

    @Test
    @DisplayName("GET /doctors/edit/{id} -> returns doctor-not-found if missing")
    void getEditDoctorPage_shouldReturnNotFoundView_whenNotFound() throws Exception
    {
        when(doctorService.getDoctorById(999L))
                .thenThrow(new DoctorNotFoundException("Doctor not found with id 999"));

        mockMvc.perform(get("/doctors/edit/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctor-not-found"))
                .andExpect(model().attribute("errorMessage", "Doctor not found with id 999"));
    }

    @Test
    @DisplayName("POST /doctors/edit/{id} -> redirects to /doctors on success")
    void editDoctor_shouldRedirect_whenValid() throws Exception {
        doNothing().when(doctorService).updateDoctor(eq(1L), any(DoctorRequestDTO.class));

        mockMvc.perform(post("/doctors/edit/1")
                        .with(csrf())
                        .param("firstName", "Gregory")
                        .param("lastName", "House")
                        .param("specialty", "Diagnostics")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));
    }

    @Test
    @DisplayName("POST /doctors/edit/{id} -> returns edit-doctor if doctor not found")
    void editDoctor_shouldReturnEditView_whenNotFound() throws Exception
    {
        // Stub to throw DoctorNotFoundException
        doThrow(new DoctorNotFoundException("Not Found"))
                .when(doctorService).updateDoctor(eq(1L), any(DoctorRequestDTO.class));

        // Provide valid data for all required fields to pass validation:
        mockMvc.perform(post("/doctors/edit/1")
                        .with(csrf())
                        .param("firstName", "Someone")
                        .param("lastName", "Else")
                        .param("specialty", "Cardiology") // MUST NOT be blank!
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/edit-doctor"))
                .andExpect(model().attribute("errorMessage", "Not Found"));
    }

    @Test
    @DisplayName("POST /doctors/edit/{id} -> returns form if validation fails")
    void editDoctor_shouldReturnForm_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/doctors/edit/1")
                        .with(csrf())
                        .param("firstName", "") // blank => triggers validation
                        .param("lastName", "LastName")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/edit-doctor"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("doctor", "firstName"));
    }

    @Test
    @DisplayName("GET /doctors/delete/{id} -> redirects to /doctors if found")
    void deleteDoctor_shouldRedirect_whenFound() throws Exception
    {
        doNothing().when(doctorService).deleteDoctor(1L);

        mockMvc.perform(get("/doctors/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));
    }

    @Test
    @DisplayName("GET /doctors/delete/{id} -> returns doctor-not-found if missing")
    void deleteDoctor_shouldReturnNotFoundView_whenNotFound() throws Exception
    {
        doThrow(new DoctorNotFoundException("No doc with id 999"))
                .when(doctorService).deleteDoctor(999L);

        mockMvc.perform(get("/doctors/delete/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctor-not-found"))
                .andExpect(model().attribute("errorMessage", "No doc with id 999"));
    }

    @Test
    @DisplayName("GET /doctors/with-patients -> returns 'doctor/doctors-with-patients' + list")
    void getDoctorsWithPatients_shouldReturnViewAndModel() throws Exception {
        List<DoctorWithPatientsDTO> mockList = List.of(
                new DoctorWithPatientsDTO(new DoctorResponseDTO(1L, "Gregory", "House", "Diagnostics"),
                        List.of(new PatientResponseDTO(1L, "John", "Doe", true, new DoctorResponseDTO(1L, "Petar", "Petrov", "Cardiology"))))
        );
        when(doctorService.getAllDoctorsWithPatients()).thenReturn(mockList);

        mockMvc.perform(get("/doctors/with-patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctors-with-patients"))
                .andExpect(model().attribute("doctorsWithPatients", mockList));
    }

    @Test
    @DisplayName("GET /doctors/examination-counts -> returns 'doctor/examination-counts' with list of counts")
    void getDoctorExaminationCounts_shouldReturnViewAndModel() throws Exception {
        List<DoctorExaminationCountDTO> mockCounts = List.of(
                new DoctorExaminationCountDTO() {
                    @Override
                    public Long getDoctorId() { return 1L; }

                    public String getDoctorName() { return "Dr. House"; }
                    @Override
                    public Long getExaminationCount() { return 10L; }
                }
        );
        when(examinationService.getExaminationCountsByDoctor()).thenReturn(mockCounts);

        mockMvc.perform(get("/doctors/examination-counts"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/examination-counts"))
                .andExpect(model().attribute("counts", mockCounts));
    }
}