package com.example.medical_record.data.repositories;

import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
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
class ExaminationRepositoryTest {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Doctor defaultDoctor;
    private Diagnosis defaultDiagnosis;
    private Patient defaultPatient;

    @BeforeEach
    void setUp()
    {
        defaultDoctor = Doctor.builder()
                .firstName("John")
                .lastName("Smith")
                .specialty("General")
                .build();
        testEntityManager.persistAndFlush(defaultDoctor);


        defaultDiagnosis = Diagnosis.builder()
                .description("Flu")
                .build();
        testEntityManager.persistAndFlush(defaultDiagnosis);


        defaultPatient = Patient.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .build();

        testEntityManager.persistAndFlush(defaultPatient);
    }

    @Test
    void findAllExaminationByIdAndIsDeletedFalse()
    {
        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Flu treatment")
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .isDeleted(false)
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .diagnosis(defaultDiagnosis)
                .build();

        testEntityManager.persistAndFlush(examination);

        Optional<Examination> result = examinationRepository.findByIdAndIsDeletedFalse(examination.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTreatmentDescription()).isEqualTo("Flu treatment");
    }

    @Test
    void findAllExaminationByIdAndIsDeletedTrue()
    {
        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Flu treatment")
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .isDeleted(true)
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .diagnosis(defaultDiagnosis)
                .build();

        testEntityManager.persistAndFlush(examination);

        Optional<Examination> result = examinationRepository.findByIdAndIsDeletedFalse(examination.getId());

        assertThat(result).isNotPresent();
    }

    @Test
    void getExaminationCountsByDoctor()
    {
        createExamination(defaultDoctor, defaultDiagnosis);
        createExamination(defaultDoctor, defaultDiagnosis);

        List<DoctorExaminationCountDTO> result = examinationRepository.getExaminationCountsByDoctor();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExaminationCount()).isEqualTo(2);
    }

    private void createExamination(Doctor doctor, Diagnosis diagnosis)
    {
        Examination examination = Examination.builder()
                .examinationDate(LocalDate.now())
                .treatmentDescription("Test treatment")
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .isDeleted(false)
                .patient(defaultPatient)
                .doctor(doctor)
                .diagnosis(diagnosis)
                .build();
        testEntityManager.persistAndFlush(examination);
    }
}

