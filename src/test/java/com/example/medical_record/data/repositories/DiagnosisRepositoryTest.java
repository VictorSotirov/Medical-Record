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
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DiagnosisRepositoryTest
{
    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Patient defaultPatient;
    private Doctor defaultDoctor;

    @BeforeEach
    void setUp()
    {
        defaultPatient = Patient.builder()
                .firstName("Petar")
                .lastName("Petrov")
                .build();

        testEntityManager.persistAndFlush(defaultPatient);


        defaultDoctor = Doctor.builder()
                .firstName("Dr.")
                .lastName("Ivanov")
                .specialty("General")
                .build();

        testEntityManager.persistAndFlush(defaultDoctor);


    }

    @Test
    public void findByDescription() {
        Diagnosis diagnosis = Diagnosis.builder()
                .description("Test Diagnosis")
                .build();

        diagnosisRepository.save(diagnosis);

        Optional<Diagnosis> result = diagnosisRepository.findByDescription("Test Diagnosis");

        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Test Diagnosis");
    }

    @Test
    public void findByDescriptionNotFound() {
        Diagnosis diagnosis = Diagnosis.builder()
                .description("Test Diagnosis")
                .build();

        diagnosisRepository.save(diagnosis);

        Optional<Diagnosis> result = diagnosisRepository.findByDescription("Test");

        assertThat(result).isNotPresent();
    }

    @Test
    public void findMostCommonDiagnosesShouldReturnDiagnosesOrderedByExaminationCount()
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

    private void createExamination(Diagnosis diagnosis) {
        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Treatment for " + diagnosis.getDescription())
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .diagnosis(diagnosis)
                .patient(defaultPatient) // Reuse patient from @BeforeEach
                .doctor(defaultDoctor)  // Reuse doctor from @BeforeEach
                .build();
        testEntityManager.persistAndFlush(examination);
    }
}
