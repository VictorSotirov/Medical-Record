package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest
{

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findSingleDoctorByIdAndIsDeletedFalse()
    {
        Doctor doctor = Doctor.builder()
                .firstName("Petar")
                .lastName("Petrob")
                .specialty("Cardiology")
                .isDeleted(false)
                .build();

        testEntityManager.persistAndFlush(doctor);


        Optional<Doctor> result = doctorRepository.findByIdAndIsDeletedFalse(doctor.getId());


        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Petar");
    }

    @Test
    void findSingleDoctorByIdAndIsDeletedTrue()
    {
        Doctor doctor = Doctor.builder()
                .firstName("Petar")
                .lastName("Petrob")
                .specialty("Cardiology")
                .isDeleted(true)
                .build();

        testEntityManager.persistAndFlush(doctor);


        Optional<Doctor> result = doctorRepository.findByIdAndIsDeletedFalse(doctor.getId());


        assertThat(result).isNotPresent();
    }

    @Test
    void findSingleDoctorByIdAndIsDeletedFalseNotFound()
    {
        Optional<Doctor> result = doctorRepository.findByIdAndIsDeletedFalse(Long.valueOf(2));

        assertThat(result).isNotPresent();
    }



    @Test
    void findAllDoctorsByIsDeletedFalse()
    {
        Doctor doctor1 = Doctor.builder().firstName("John").lastName("Doe").specialty("Cardiology").isDeleted(false).build();
        Doctor doctor2 = Doctor.builder().firstName("Jane").lastName("Smith").specialty("Neurology").isDeleted(false).build();
        Doctor doctor3 = Doctor.builder().firstName("Mark").lastName("Brown").specialty("Orthopedics").isDeleted(true).build();

        testEntityManager.persistAndFlush(doctor1);
        testEntityManager.persistAndFlush(doctor2);
        testEntityManager.persistAndFlush(doctor3);

        List<Doctor> result = doctorRepository.findByIsDeletedFalse();

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllDoctorsByIsDeletedTrue()
    {
        Doctor doctor1 = Doctor.builder().firstName("John").lastName("Doe").specialty("Cardiology").isDeleted(true).build();
        Doctor doctor2 = Doctor.builder().firstName("Jane").lastName("Smith").specialty("Neurology").isDeleted(true).build();
        Doctor doctor3 = Doctor.builder().firstName("Mark").lastName("Brown").specialty("Orthopedics").isDeleted(true).build();

        testEntityManager.persistAndFlush(doctor1);
        testEntityManager.persistAndFlush(doctor2);
        testEntityManager.persistAndFlush(doctor3);

        List<Doctor> result = doctorRepository.findByIsDeletedFalse();

        assertThat(result).hasSize(0);
    }

    @Test
    void findAllDoctorsByIsDeletedFalseNoDoctorsPresent()
    {
        List<Doctor> result = doctorRepository.findByIsDeletedFalse();

        assertThat(result).hasSize(0);
    }
}
