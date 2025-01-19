package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.diagnosis.*;
import com.example.medical_record.exceptions.diagnosis.DiagnosisAlreadyExistsException;
import com.example.medical_record.exceptions.diagnosis.DiagnosisNotFoundException;
import com.example.medical_record.services.DiagnosisService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN") //
@WebMvcTest(DiagnosisController.class)
class DiagnosisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiagnosisService diagnosisService;

    @Test
    @DisplayName("GET /diagnoses -> returns list of diagnoses and view 'diagnosis/diagnoses'")
    void getDiagnoses_shouldReturnDiagnosesViewAndModel() throws Exception {
        // Given
        List<DiagnosisResponseDTO> mockDiagnoses = List.of(
                new DiagnosisResponseDTO(1L, "Cold"),
                new DiagnosisResponseDTO(2L, "Flu")
        );
        when(diagnosisService.getAllDiagnoses()).thenReturn(mockDiagnoses);

        // When/Then
        mockMvc.perform(get("/diagnoses"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/diagnoses"))
                .andExpect(model().attribute("diagnoses", mockDiagnoses));
    }

    @Test
    @DisplayName("GET /diagnoses/create -> returns 'diagnosis/create-diagnosis' view with empty form")
    void getCreateDiagnosisPage_shouldReturnCreateForm() throws Exception {
        mockMvc.perform(get("/diagnoses/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/create-diagnosis"))
                .andExpect(model().attributeExists("diagnosis"));
    }

    @Test
    @DisplayName("POST /diagnoses/create -> redirects to /diagnoses on success")
    void createDiagnosis_shouldRedirect_whenValid() throws Exception {
        doNothing().when(diagnosisService).createDiagnosis(any(DiagnosisRequestDTO.class));

        mockMvc.perform(post("/diagnoses/create")
                        .with(csrf())
                        .param("description", "NewValidDiagnosis")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/diagnoses"));

        verify(diagnosisService).createDiagnosis(any(DiagnosisRequestDTO.class));
    }

    @Test
    @DisplayName("POST /diagnoses/create -> stays on form if diagnosis already exists")
    void createDiagnosis_shouldReturnForm_whenDiagnosisAlreadyExists() throws Exception {
        doThrow(new DiagnosisAlreadyExistsException("Diagnosis already exists"))
                .when(diagnosisService)
                .createDiagnosis(any(DiagnosisRequestDTO.class));

        mockMvc.perform(post("/diagnoses/create")
                        .with(csrf())
                        .param("description", "DuplicateDiagnosis")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/create-diagnosis"))
                .andExpect(model().attribute("errorMessage", "Diagnosis already exists"));
    }

    @Test
    @DisplayName("POST /diagnoses/create -> stays on form if blank description triggers validation error")
    void createDiagnosis_shouldReturnForm_whenDescriptionIsBlank() throws Exception {
        // Because @NotBlank is on 'description'
        mockMvc.perform(post("/diagnoses/create")
                        .with(csrf())
                        .param("description", "") // blank => validation error
                )
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/create-diagnosis"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("diagnosis", "description"));
    }

    @Test
    @DisplayName("GET /diagnoses/edit/{id} -> returns edit view if found")
    void getEditDiagnosisPage_shouldReturnEditView_whenDiagnosisExists() throws Exception {
        DiagnosisResponseDTO existingDiag = new DiagnosisResponseDTO(1L, "Flu");
        when(diagnosisService.getDiagnosisById(1L)).thenReturn(existingDiag);

        mockMvc.perform(get("/diagnoses/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/edit-diagnosis"))
                .andExpect(model().attribute("diagnosis", existingDiag));
    }

    @Test
    @DisplayName("GET /diagnoses/edit/{id} -> returns 'diagnosis-not-found' if not found")
    void getEditDiagnosisPage_shouldReturnNotFoundView_whenNotFound() throws Exception {
        when(diagnosisService.getDiagnosisById(999L))
                .thenThrow(new DiagnosisNotFoundException("Diagnosis with id 999 not found"));

        mockMvc.perform(get("/diagnoses/edit/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/diagnosis-not-found"))
                .andExpect(model().attribute("errorMessage", "Diagnosis with id 999 not found"));
    }

    @Test
    @DisplayName("POST /diagnoses/edit/{id} -> redirect to /diagnoses on success")
    void editDiagnosis_shouldRedirect_whenValid() throws Exception {
        doNothing().when(diagnosisService).updateDiagnosis(eq(1L), any(DiagnosisRequestDTO.class));

        mockMvc.perform(post("/diagnoses/edit/1")
                        .with(csrf())
                        .param("description", "UpdatedDiagnosis")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/diagnoses"));

        verify(diagnosisService).updateDiagnosis(eq(1L), any(DiagnosisRequestDTO.class));
    }

    @Test
    @DisplayName("POST /diagnoses/edit/{id} -> stays on edit form if diagnosis already exists")
    void editDiagnosis_shouldReturnEditView_whenDiagnosisAlreadyExists() throws Exception {
        doThrow(new DiagnosisAlreadyExistsException("Already exists"))
                .when(diagnosisService).updateDiagnosis(eq(1L), any(DiagnosisRequestDTO.class));

        mockMvc.perform(post("/diagnoses/edit/1")
                        .with(csrf())
                        .param("description", "Duplicate")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/edit-diagnosis"))
                .andExpect(model().attribute("errorMessage", "Already exists"));
    }

    @Test
    @DisplayName("POST /diagnoses/edit/{id} -> stays on edit form if diagnosis not found")
    void editDiagnosis_shouldReturnEditView_whenDiagnosisNotFound() throws Exception {
        doThrow(new DiagnosisNotFoundException("Not Found"))
                .when(diagnosisService).updateDiagnosis(eq(1L), any(DiagnosisRequestDTO.class));

        mockMvc.perform(post("/diagnoses/edit/1")
                        .with(csrf())
                        .param("description", "SomeDesc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/edit-diagnosis"))
                .andExpect(model().attribute("errorMessage", "Not Found"));
    }

    @Test
    @DisplayName("POST /diagnoses/edit/{id} -> stays on edit form if blank description triggers validation error")
    void editDiagnosis_shouldReturnForm_whenBlankDescription() throws Exception {
        mockMvc.perform(post("/diagnoses/edit/1")
                        .with(csrf())
                        .param("description", "") // blank => validation error
                )
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/edit-diagnosis"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("diagnosis", "description"));
    }

    @Test
    @DisplayName("GET /diagnoses/delete/{id} -> redirects if found")
    void deleteDiagnosis_shouldRedirect_whenFound() throws Exception {
        doNothing().when(diagnosisService).deleteDiagnosis(1L);

        mockMvc.perform(get("/diagnoses/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/diagnoses"));
    }

    @Test
    @DisplayName("GET /diagnoses/delete/{id} -> returns 'diagnosis-not-found' if not found")
    void deleteDiagnosis_shouldReturnNotFoundView_whenNotFound() throws Exception {
        doThrow(new DiagnosisNotFoundException("Diagnosis not found"))
                .when(diagnosisService).deleteDiagnosis(999L);

        mockMvc.perform(get("/diagnoses/delete/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/diagnosis-not-found"))
                .andExpect(model().attribute("errorMessage", "Diagnosis not found"));
    }

    @Test
    @DisplayName("GET /diagnoses/most-common -> returns 'diagnosis/most-common-diagnoses' view with list")
    void getMostCommonDiagnoses_shouldReturnViewAndModel() throws Exception {
        List<DiagnosisFrequencyDTO> mockFrequencies = List.of(
                new DiagnosisFrequencyDTO(new DiagnosisResponseDTO(1L, "Flu"), 10L),
                new DiagnosisFrequencyDTO(new DiagnosisResponseDTO(2L, "Cold"), 5L)
        );

        when(diagnosisService.getMostCommonDiagnoses()).thenReturn(mockFrequencies);

        mockMvc.perform(get("/diagnoses/most-common"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/most-common-diagnoses"))
                .andExpect(model().attribute("mostCommonDiagnoses", mockFrequencies));
    }
}