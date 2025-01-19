package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.doctor.*;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.entities.auth.*;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.RoleRepository;
import com.example.medical_record.data.repositories.UserRepository;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest
{
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void createDoctor_shouldSaveDoctorAndUser()
    {
        DoctorRequestDTO request = new DoctorRequestDTO();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialty("Surgery");

        Doctor doctor = new Doctor();

        doctor.setId(1L);
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setDeleted(false);

        Role doctorRole = new Role();

        doctorRole.setAuthority("DOCTOR");

        when(roleRepository.findByAuthority("DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted-password");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);


        doctorService.createDoctor(request);


        verify(doctorRepository).save(any(Doctor.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateDoctor_shouldUpdateDoctorDetails() throws DoctorNotFoundException {
        // Arrange
        Long doctorId = 1L;
        Doctor existingDoctor = Doctor.builder()
                .id(doctorId)
                .firstName("John")
                .lastName("Smith")
                .specialty("Cardiology")
                .isDeleted(false)
                .build();

        DoctorRequestDTO updatedRequest = new DoctorRequestDTO();

        updatedRequest.setFirstName("Jane");
        updatedRequest.setLastName("Doe");
        updatedRequest.setSpecialty("Surgery");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));

        doctorService.updateDoctor(doctorId, updatedRequest);


        assertThat(existingDoctor.getFirstName()).isEqualTo("Jane");
        assertThat(existingDoctor.getLastName()).isEqualTo("Doe");
        assertThat(existingDoctor.getSpecialty()).isEqualTo("Surgery");
        verify(doctorRepository).save(existingDoctor);
    }

    @Test
    void deleteDoctor_shouldMarkDoctorAsDeleted() throws DoctorNotFoundException
    {
        Long doctorId = 1L;
        Doctor existingDoctor = Doctor.builder()
                .id(doctorId)
                .firstName("John")
                .lastName("Doe")
                .specialty("Surgery")
                .isDeleted(false)
                .build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));

        doctorService.deleteDoctor(doctorId);


        assertThat(existingDoctor.isDeleted()).isTrue();
        verify(doctorRepository).save(existingDoctor);
    }

    @Test
    void getDoctorById_shouldReturnDoctorResponseDTO() throws DoctorNotFoundException
    {
        Long doctorId = 1L;
        Doctor doctor = Doctor.builder()
                .id(doctorId)
                .firstName("John")
                .lastName("Doe")
                .specialty("Surgery")
                .isDeleted(false)
                .build();

        when(doctorRepository.findByIdAndIsDeletedFalse(doctorId)).thenReturn(Optional.of(doctor));

        DoctorResponseDTO result = doctorService.getDoctorById(doctorId);


        assertThat(result.getId()).isEqualTo(doctorId);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getSpecialty()).isEqualTo("Surgery");
    }

    @Test
    void getAllDoctors_shouldReturnListOfDoctorResponseDTOs()
    {
        List<Doctor> doctors = List.of(
                Doctor.builder().id(1L).firstName("John").lastName("Doe").specialty("Surgery").isDeleted(false).build(),
                Doctor.builder().id(2L).firstName("Jane").lastName("Doe").specialty("Cardiology").isDeleted(false).build()
        );

        when(doctorRepository.findByIsDeletedFalse()).thenReturn(doctors);

        List<DoctorResponseDTO> result = doctorService.getAllDoctors();


        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getSpecialty()).isEqualTo("Cardiology");
    }

    @Test
    void getAllDoctorsWithPatients_shouldReturnDoctorsWithPatients()
    {
        Patient patient = Patient.builder().id(1L).firstName("Alice").lastName("Smith").isDeleted(false).build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .specialty("Surgery")
                .isDeleted(false)
                .registeredPatients(Set.of(patient))
                .build();

        when(doctorRepository.findByIsDeletedFalse()).thenReturn(List.of(doctor));

        List<DoctorWithPatientsDTO> result = doctorService.getAllDoctorsWithPatients();


        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctor().getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getPatients().get(0).getFirstName()).isEqualTo("Alice");
    }

}