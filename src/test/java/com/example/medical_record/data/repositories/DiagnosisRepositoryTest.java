package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Examination;
import com.example.medical_record.data.entities.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DiagnosisRepositoryTest
{
    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findByDescription()
    {
        Diagnosis diagnosis = Diagnosis.builder()
                .description("Test Diagnosis")
                .build();

        diagnosisRepository.save(diagnosis);

        Optional<Diagnosis> result = diagnosisRepository.findByDescription("Test Diagnosis");

        assertThat(result).isPresent();

        assertThat(result.get().getDescription()).isEqualTo("Test Diagnosis");
    }

    @Test
    public void findByDescriptionNotFound()
    {
        Diagnosis diagnosis = Diagnosis.builder()
                .description("Test Diagnosis")
                .build();

        diagnosisRepository.save(diagnosis);

        Optional<Diagnosis> result = diagnosisRepository.findByDescription("Test");

        assertThat(result).isNotPresent();
    }

    @Test
    public void findMostCommonDiagnoses_ShouldReturnDiagnosesOrderedByExaminationCount()
    {
        Diagnosis diagnosis1 = Diagnosis.builder().description("Diagnosis 1").build();
        Diagnosis diagnosis2 = Diagnosis.builder().description("Diagnosis 2").build();

        testEntityManager.persistAndFlush(diagnosis1);
        testEntityManager.persistAndFlush(diagnosis2);

        createExamination(diagnosis1);
        createExamination(diagnosis1);
        createExamination(diagnosis2);
        createExamination(diagnosis2);
        createExamination(diagnosis2);

        List<Object[]> result = diagnosisRepository.findMostCommonDiagnoses();

        assertThat(result.get(0)[0]).isEqualTo(diagnosis2); // Most common diagnosis
        assertThat(result.get(0)[1]).isEqualTo(3L); // Count of examinations
    }

    private void createExamination(Diagnosis diagnosis)
    {
        Patient patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        testEntityManager.persistAndFlush(patient);

        Doctor doctor = Doctor.builder()
                .firstName("Dr.")
                .lastName("Smith")
                .specialty("General")
                .build();
        testEntityManager.persistAndFlush(doctor);

        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Treatment for " + diagnosis.getDescription())
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .diagnosis(diagnosis)
                .patient(patient) // Replace with a persisted Patient entity if necessary
                .doctor(doctor)  // Replace with a persisted Doctor entity if necessary
                .build();
        testEntityManager.persistAndFlush(examination);
    }
}