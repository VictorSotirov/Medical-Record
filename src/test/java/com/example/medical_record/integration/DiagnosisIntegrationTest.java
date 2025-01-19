package com.example.medical_record.integration;

import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
public class DiagnosisIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @BeforeEach
    void setUp()
    {
        diagnosisRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /diagnoses/create -> Should create new diagnosis and redirect to /diagnoses")
    void createDiagnosis_shouldRedirectOnSuccess() throws Exception
    {
        mockMvc.perform(post("/diagnoses/create")
                        .param("description", "Flu")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/diagnoses"));

        Optional<Diagnosis> saved = diagnosisRepository.findByDescription("Flu");
        assertThat(saved).isPresent();
        assertThat(saved.get().getDescription()).isEqualTo("Flu");
    }

    @Test
    @DisplayName("POST /diagnoses/create -> returns same form if diagnosis already exists")
    void createDiagnosis_shouldReturnFormWhenAlreadyExists() throws Exception
    {
        diagnosisRepository.save(Diagnosis.builder().description("Flu").build());

        mockMvc.perform(post("/diagnoses/create")
                        .param("description", "Flu")
                        .with(csrf()))
                .andExpect(status().isOk())  // stays on same page => 200
                .andExpect(view().name("diagnosis/create-diagnosis"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Diagnosis already exists"));
    }

    @Test
    @DisplayName("GET /diagnoses -> returns 'diagnosis/diagnoses' with all diagnoses in model")
    void getDiagnoses_shouldReturnList() throws Exception
    {
        diagnosisRepository.save(Diagnosis.builder().description("Flu").build());
        diagnosisRepository.save(Diagnosis.builder().description("Cold").build());

        mockMvc.perform(get("/diagnoses"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/diagnoses"))
                .andExpect(model().attributeExists("diagnoses"))
                .andExpect(model().attribute("diagnoses", hasSize(2)));
    }

    @Test
    @DisplayName("POST /diagnoses/edit/{id} -> redirects on success")
    void editDiagnosis_shouldRedirectOnSuccess() throws Exception
    {
        Diagnosis saved = diagnosisRepository.save(Diagnosis.builder().description("Original").build());


        mockMvc.perform(post("/diagnoses/edit/{id}", saved.getId())
                        .param("description", "Updated")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/diagnoses"));


        Diagnosis updated = diagnosisRepository.findById(saved.getId()).get();
        assertThat(updated.getDescription()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("POST /diagnoses/edit/999 -> returns edit page with error if not found")
    void editDiagnosis_shouldReturnEditPageWhenNotFound() throws Exception
    {
        mockMvc.perform(post("/diagnoses/edit/999")
                        .param("description", "DoesNotMatter")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/edit-diagnosis"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Diagnosis with id: 999 not found."));
    }

    @Test
    @DisplayName("GET /diagnoses/most-common -> returns 'diagnosis/most-common-diagnoses' view")
    void getMostCommonDiagnoses_shouldReturnViewAndModel() throws Exception
    {
        mockMvc.perform(get("/diagnoses/most-common"))
                .andExpect(status().isOk())
                .andExpect(view().name("diagnosis/most-common-diagnoses"))
                .andExpect(model().attributeExists("mostCommonDiagnoses"));
    }
}
