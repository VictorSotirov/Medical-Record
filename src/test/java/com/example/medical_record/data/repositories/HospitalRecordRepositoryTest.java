package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.HospitalRecord;
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
class HospitalRecordRepositoryTest
{
    @Autowired
    private HospitalRecordRepository hospitalRecordRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Doctor defaultDoctor;
    private Patient defaultPatient;

    @BeforeEach
    void setUp()
    {
        defaultDoctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .specialty("Surgery")
                .isDeleted(false)
                .build();
        testEntityManager.persistAndFlush(defaultDoctor);

        defaultPatient = Patient.builder()
                .firstName("Jane")
                .lastName("Doe")
                .isHealthInsurancePaid(true)
                .isDeleted(false)
                .build();

        testEntityManager.persistAndFlush(defaultPatient);

        HospitalRecord hospitalRecord1 = HospitalRecord.builder()
                .admissionDate(LocalDate.of(2025, 1, 10))
                .dischargeDate(LocalDate.of(2025, 1, 15))
                .isDeleted(false)
                .doctor(defaultDoctor)
                .patient(defaultPatient)
                .build();

        testEntityManager.persistAndFlush(hospitalRecord1);

        HospitalRecord hospitalRecord2 = HospitalRecord.builder()
                .admissionDate(LocalDate.of(2025, 1, 12))
                .dischargeDate(LocalDate.of(2025, 1, 17))
                .isDeleted(false)
                .doctor(defaultDoctor)
                .patient(defaultPatient)
                .build();

        testEntityManager.persistAndFlush(hospitalRecord2);

        HospitalRecord hospitalRecord3 = HospitalRecord.builder()
                .admissionDate(LocalDate.of(2025, 1, 18))
                .dischargeDate(LocalDate.of(2025, 1, 22))
                .isDeleted(true)
                .doctor(defaultDoctor)
                .patient(defaultPatient)
                .build();

        testEntityManager.persistAndFlush(hospitalRecord2);
    }

    @Test
    void findHospitalRecordByIdAndIsDeletedFalse()
    {
        List<HospitalRecord> records = hospitalRecordRepository.findByIsDeletedFalse();
        assertThat(records).isNotEmpty();

        Optional<HospitalRecord> result = hospitalRecordRepository.findByIdAndIsDeletedFalse(records.get(0).getId());
        assertThat(result).isPresent();
        assertThat(result.get().getAdmissionDate()).isEqualTo(LocalDate.of(2025, 1, 10));
    }

    @Test
    void findAllByIsDeletedFalse() {
        List<HospitalRecord> result = hospitalRecordRepository.findByIsDeletedFalse();
        assertThat(result).hasSize(2); // Records created in @BeforeEach
    }

    @Test
    void findByAdmissionDateBetweenAndIsDeletedFalse()
    {
        List<HospitalRecord> result = hospitalRecordRepository.findByAdmissionDateBetweenAndIsDeletedFalse(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertThat(result).hasSize(2);
    }

    @Test
    void findMonthWithMostHospitalRecords()
    {
        List<Object[]> result = hospitalRecordRepository.findMonthWithMostHospitalRecords();
        assertThat(result.get(0)[0]).isEqualTo(1); // January
        assertThat(result.get(0)[1]).isEqualTo(2L); // Record count
    }

    @Test
    void findRecordsByMonth()
    {
        List<HospitalRecord> result = hospitalRecordRepository.findRecordsByMonth(1); // January
        assertThat(result).hasSize(2);
    }

    @Test
    void findDoctorWithMostRecords()
    {
        List<Object[]> result = hospitalRecordRepository.findDoctorWithMostRecords();
        assertThat(result.get(0)[0]).isEqualTo(defaultDoctor.getId()); // Default doctor
        assertThat(result.get(0)[4]).isEqualTo(2L); // Record count
    }
}
