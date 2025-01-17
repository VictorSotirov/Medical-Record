package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Examination;
import com.example.medical_record.data.entities.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Doctor defaultDoctor;
    private Diagnosis defaultDiagnosis;

    private Patient defaultPatient;

    @BeforeEach
    void setUp()
    {
        // Create a default doctor
        defaultDoctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .specialty("General")
                .isDeleted(false)
                .build();
        testEntityManager.persistAndFlush(defaultDoctor);

        // Create a default diagnosis
        defaultDiagnosis = Diagnosis.builder()
                .description("Flu")
                .build();
        testEntityManager.persistAndFlush(defaultDiagnosis);

        defaultPatient = Patient.builder()
                .firstName("Alice")
                .lastName("Smith")
                .isDeleted(false)
                .personalDoctor(defaultDoctor)
                .build();
        testEntityManager.persistAndFlush(defaultPatient);
    }

    @Test
    void findByIdAndIsDeletedFalse_ShouldReturnPatient_WhenPatientExists() {
        Optional<Patient> result = patientRepository.findByIdAndIsDeletedFalse(defaultPatient.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Alice");
        assertThat(result.get().isDeleted()).isFalse();
    }

    @Test
    void findByIdAndIsDeletedFalse_ShouldReturnEmpty_WhenPatientIsDeleted()
    {
        defaultPatient.setDeleted(true);
        testEntityManager.persistAndFlush(defaultPatient);

        Optional<Patient> result = patientRepository.findByIdAndIsDeletedFalse(defaultPatient.getId());

        assertThat(result).isNotPresent();
    }

    @Test
    void findByIsDeletedFalse_ShouldReturnAllNonDeletedPatients() {
        createPatient("Bob", "Jones", false, defaultDoctor);
        createPatient("Charlie", "Brown", true, defaultDoctor);

        List<Patient> result = patientRepository.findByIsDeletedFalse();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Patient::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void findDistinctByExaminationsDiagnosisIdAndIsDeletedFalse_ShouldReturnPatientsWithSpecificDiagnosis() {
        createExamination(defaultPatient, defaultDiagnosis);
        Patient patient2 = createPatient("Bob", "Jones", false, defaultDoctor);
        createExamination(patient2, defaultDiagnosis);

        List<Patient> result = patientRepository.findDistinctByExaminationsDiagnosisIdAndIsDeletedFalse(defaultDiagnosis.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Patient::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void findByPersonalDoctorIdAndIsDeletedFalse_ShouldReturnPatientsForSpecificDoctor() {
        createPatient("Bob", "Jones", false, defaultDoctor);
        createPatient("Charlie", "Brown", false, createDoctor("Jane", "Doe", "Surgery"));

        List<Patient> result = patientRepository.findByPersonalDoctorIdAndIsDeletedFalse(defaultDoctor.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Patient::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    private Patient createPatient(String firstName, String lastName, boolean isDeleted, Doctor personalDoctor) {
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .isDeleted(isDeleted)
                .personalDoctor(personalDoctor)
                .build();
        testEntityManager.persistAndFlush(patient);
        return patient;
    }

    private void createExamination(Patient patient, Diagnosis diagnosis) {
        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Treatment for " + diagnosis.getDescription())
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .isDeleted(false)
                .patient(patient)
                .doctor(defaultDoctor)
                .diagnosis(diagnosis)
                .build();
        testEntityManager.persistAndFlush(examination);
    }

    private Doctor createDoctor(String firstName, String lastName, String specialty) {
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .isDeleted(false)
                .build();
        testEntityManager.persistAndFlush(doctor);
        return doctor;
    }
}
