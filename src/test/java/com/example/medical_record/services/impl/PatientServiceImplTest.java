package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.patient.PatientRequestDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.DTOs.patient.PatientsWithExaminationsDTO;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Examination;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.entities.auth.Role;
import com.example.medical_record.data.entities.auth.User;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import com.example.medical_record.data.repositories.RoleRepository;
import com.example.medical_record.data.repositories.UserRepository;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.exceptions.patient.PatientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PatientRequestDTO patientRequestDTO;
    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp()
    {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialty("General");
        doctor.setDeleted(false);

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        patient.setHealthInsurancePaid(true);
        patient.setDeleted(false);
        patient.setPersonalDoctor(doctor);

        patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setFirstName("Jane");
        patientRequestDTO.setLastName("Doe");
        patientRequestDTO.setHealthInsurancePaid(true);
        patientRequestDTO.setPersonalDoctorId(1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
    }

    @Test
    void createPatient_shouldSavePatientAndUser()
    {
        Role role = new Role();
        role.setAuthority("ROLE_PATIENT");

        when(roleRepository.findByAuthority("ROLE_PATIENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.createPatient(patientRequestDTO);

        verify(patientRepository).save(any(Patient.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePatient_shouldUpdatePatientDetails() throws PatientNotFoundException, DoctorNotFoundException
    {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(1L, patientRequestDTO);

        verify(patientRepository).save(any(Patient.class));
        assertEquals(patientRequestDTO.getFirstName(), patient.getFirstName());
        assertEquals(patientRequestDTO.getLastName(), patient.getLastName());
    }

    @Test
    void updatePatient_shouldThrowPatientNotFoundException()
    {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(1L, patientRequestDTO));
    }

    @Test
    void deletePatient_shouldMarkPatientAsDeleted() throws PatientNotFoundException {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository).save(patient);
        assertTrue(patient.isDeleted());
    }

    @Test
    void deletePatient_shouldThrowPatientNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(1L));
    }

    @Test
    void getPatientById_shouldReturnPatientResponseDTO() throws PatientNotFoundException
    {
        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(patient));

        PatientResponseDTO responseDTO = patientService.getPatientById(1L);

        assertEquals(patient.getId(), responseDTO.getId());
        assertEquals(patient.getFirstName(), responseDTO.getFirstName());
    }

    @Test
    void getPatientById_shouldThrowPatientNotFoundException() {
        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(1L));
    }

    @Test
    void getAllPatients_shouldReturnListOfPatientResponseDTO()
    {
        when(patientRepository.findByIsDeletedFalse()).thenReturn(List.of(patient));

        List<PatientResponseDTO> responseDTOs = patientService.getAllPatients();

        assertEquals(1, responseDTOs.size());
        assertEquals(patient.getId(), responseDTOs.get(0).getId());
    }

    @Test
    void getAllPatientsWithSameDiagnosis_shouldReturnListOfPatientResponseDTO() {
        when(patientRepository.findDistinctByExaminationsDiagnosisIdAndIsDeletedFalse(1L)).thenReturn(List.of(patient));

        List<PatientResponseDTO> responseDTOs = patientService.getAllPatientsWithSameDiagnosis(1L);

        assertEquals(1, responseDTOs.size());
        assertEquals(patient.getId(), responseDTOs.get(0).getId());
    }

    @Test
    void getPatientsByDoctorId_shouldReturnListOfPatientResponseDTO() {
        when(patientRepository.findByPersonalDoctorIdAndIsDeletedFalse(1L)).thenReturn(List.of(patient));

        List<PatientResponseDTO> responseDTOs = patientService.getPatientsByDoctorId(1L);

        assertEquals(1, responseDTOs.size());
        assertEquals(patient.getId(), responseDTOs.get(0).getId());
    }

    @Test
    void getAllPatientsWithExaminations_shouldReturnPatientsWithExaminationsDTO()
    {
        Examination examination = new Examination();
        examination.setId(1L);
        examination.setTreatmentDescription("Treatment");
        examination.setDeleted(false);
        patient.setExaminations(Set.of(examination));

        when(patientRepository.findByIsDeletedFalse()).thenReturn(List.of(patient));

        List<PatientsWithExaminationsDTO> results = patientService.getAllPatientsWithExaminations();

        assertEquals(1, results.size());
        assertEquals(patient.getId(), results.get(0).getPatient().getId());
        assertEquals(1, results.get(0).getExaminations().size());
    }
}
