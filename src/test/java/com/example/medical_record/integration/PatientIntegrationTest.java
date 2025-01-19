package com.example.medical_record.integration;

import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.entities.auth.Role;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import com.example.medical_record.data.repositories.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles="ADMIN")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // optional if you want to control test order
class PatientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;


    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp()
    {
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        diagnosisRepository.deleteAll();
        roleRepository.deleteAll();

        Role patientRole = new Role();
        patientRole.setAuthority("ROLE_PATIENT");
        roleRepository.save(patientRole);
    }

    @Test
    @DisplayName("GET /patients -> returns patients list + diagnoses + doctors + 'patient/patients' view")
    void getPatients_shouldReturnViewAndModel() throws Exception
    {
        Patient p = Patient.builder().firstName("John").lastName("Doe").build();
        patientRepository.save(p);


        Diagnosis diag = Diagnosis.builder().description("Flu").build();
        diagnosisRepository.save(diag);

        Doctor doc = Doctor.builder().firstName("Gregory").lastName("House").specialty("Diagnostics").build();
        doctorRepository.save(doc);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients"))
                .andExpect(model().attributeExists("patients", "diagnoses", "doctors"));
    }

    @Test
    @DisplayName("GET /patients/create -> returns 'patient/create-patient' with empty form + doctors")
    void getCreatePatientPage_shouldReturnCreateForm() throws Exception
    {
        mockMvc.perform(get("/patients/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/create-patient"))
                .andExpect(model().attributeExists("patient", "doctors"));
    }

    @Test
    @DisplayName("POST /patients/create -> success => redirect /patients")
    void createPatient_shouldRedirectOnSuccess() throws Exception
    {
        Doctor doc = doctorRepository.save(
                Doctor.builder().firstName("Gregory").lastName("House").specialty("Diagnostics").build()
        );

        mockMvc.perform(post("/patients/create")
                        .param("firstName", "Mary")
                        .param("lastName", "Smith")
                        .param("healthInsurancePaid", "true")
                        .param("personalDoctorId", doc.getId().toString()) // optional if you want a doc
                        .with(csrf()) // needed for POST if CSRF is on
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getFirstName()).isEqualTo("Mary");
    }

    @Test
    @DisplayName("POST /patients/create -> blank firstName => re-display form")
    void createPatient_shouldReturnForm_whenValidationFails() throws Exception
    {
        mockMvc.perform(post("/patients/create")
                        .param("firstName", "")
                        .param("lastName", "Smith")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/create-patient"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("patient", "firstName"));
    }

    @Test
    @DisplayName("GET /patients/edit/{id} -> returns edit form if found")
    void getEditPatientPage_shouldReturnEditForm_whenFound() throws Exception
    {
        Patient saved = patientRepository.save(
                Patient.builder().firstName("John").lastName("Doe").build()
        );

        mockMvc.perform(get("/patients/edit/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/edit-patient"))
                .andExpect(model().attributeExists("patient", "patientId", "doctors"));
    }

    @Test
    @DisplayName("GET /patients/edit/{id} -> returns 'patient/patient-not-found' if missing")
    void getEditPatientPage_shouldReturnNotFound_whenNotFound() throws Exception
    {
        mockMvc.perform(get("/patients/edit/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Patient with id 999 not found."));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> success => redirect")
    void editPatient_shouldRedirectOnSuccess() throws Exception
    {
        Patient saved = patientRepository.save(Patient.builder().firstName("Original").lastName("Name").build());

        mockMvc.perform(post("/patients/edit/" + saved.getId())
                        .param("firstName", "Updated")
                        .param("lastName", "Name")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        Patient updated = patientRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> blank lastName => re-display form")
    void editPatient_shouldReturnForm_whenValidationFails() throws Exception
    {
        Patient saved = patientRepository.save(
                Patient.builder().firstName("Valid").lastName("Name").build()
        );

        mockMvc.perform(post("/patients/edit/" + saved.getId())
                        .param("firstName", "StillValid")
                        .param("lastName", "") // triggers NotBlank error
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/edit-patient"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("patient", "lastName"));
    }

    @Test
    @DisplayName("POST /patients/edit/{id} -> not found => patient-not-found")
    void editPatient_shouldReturnNotFound_whenPatientMissing() throws Exception
    {
        mockMvc.perform(post("/patients/edit/999")
                        .param("firstName", "Anything")
                        .param("lastName", "AlsoAnything")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /patients/delete/{id} -> success => redirect to /patients")
    void deletePatient_shouldRedirect_whenFound() throws Exception
    {
        Patient saved = patientRepository.save(
                Patient.builder().firstName("Will").lastName("Delete").build()
        );

        mockMvc.perform(get("/patients/delete/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        Patient updated = patientRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("GET /patients/delete/{id} -> not found => patient/patient-not-found")
    void deletePatient_shouldReturnNotFound_whenMissing() throws Exception
    {
        mockMvc.perform(get("/patients/delete/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-not-found"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles={"DOCTOR"})
    @DisplayName("GET /patients/filter-by-diagnosis?diagnosisId= -> returns 'patient/patients'")
    void filterPatientsByDiagnosis_shouldReturnView() throws Exception
    {
        Diagnosis diag = diagnosisRepository.save(Diagnosis.builder().description("Flu").build());

        mockMvc.perform(get("/patients/filter-by-diagnosis")
                        .param("diagnosisId", diag.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patients"))
                .andExpect(model().attributeExists("patients", "diagnoses", "selectedDiagnosisId"));
    }
}
