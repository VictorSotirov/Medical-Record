package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.DTOs.examination.*;
import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Examination;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.ExaminationRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceImplTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @InjectMocks
    private ExaminationServiceImpl examinationService;

    private Patient samplePatient;
    private Doctor sampleDoctor;
    private Diagnosis sampleDiagnosis;
    private Examination sampleExamination;

    @BeforeEach
    void setUp() {
        // Create a sample Patient, Doctor, Diagnosis, and Examination to reuse in tests
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .isDeleted(false)
                .build();

        sampleDoctor = Doctor.builder()
                .id(2L)
                .firstName("Dr. Smith")
                .lastName("Jones")
                .specialty("General")
                .isDeleted(false)
                .build();

        sampleDiagnosis = Diagnosis.builder()
                .id(3L)
                .description("Sample Diagnosis")
                .build();

        sampleExamination = Examination.builder()
                .id(100L)
                .examinationDate(LocalDate.now())
                .treatmentDescription("Some treatment")
                .sickLeaveDays(5)
                .sickLeaveStartDate(LocalDate.now().plusDays(1))
                .patient(samplePatient)
                .doctor(sampleDoctor)
                .diagnosis(sampleDiagnosis)
                .isDeleted(false)
                .build();

        lenient().when(patientRepository.findByIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(samplePatient));

        lenient().when(doctorRepository.findByIdAndIsDeletedFalse(2L))
                .thenReturn(Optional.of(sampleDoctor));

        lenient().when(diagnosisRepository.findById(3L))
                .thenReturn(Optional.of(sampleDiagnosis));
    }
    @Test
    void createExamination_shouldSaveExamination_whenPatientDoctorDiagnosisExist()
    {
        ExaminationRequestDTO requestDto = new ExaminationRequestDTO();
        requestDto.setExaminationDate(LocalDate.now());
        requestDto.setTreatmentDescription("Treatment desc");
        requestDto.setSickLeaveDays(3);
        requestDto.setSickLeaveStartDate(LocalDate.now().plusDays(1));
        requestDto.setPatientId(samplePatient.getId());
        requestDto.setDoctorId(sampleDoctor.getId());
        requestDto.setDiagnosisId(sampleDiagnosis.getId());


        when(patientRepository.findByIdAndIsDeletedFalse(samplePatient.getId()))
                .thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findByIdAndIsDeletedFalse(sampleDoctor.getId()))
                .thenReturn(Optional.of(sampleDoctor));
        when(diagnosisRepository.findById(sampleDiagnosis.getId()))
                .thenReturn(Optional.of(sampleDiagnosis));


        when(examinationRepository.save(any(Examination.class)))
                .thenReturn(sampleExamination);

        examinationService.createExamination(requestDto);

        verify(examinationRepository, times(1)).save(any(Examination.class));
    }

    @Test
    void createExamination_shouldThrowRuntimeException_whenPatientNotFound()
    {
        ExaminationRequestDTO requestDto = new ExaminationRequestDTO();
        requestDto.setPatientId(999L);


        assertThatThrownBy(() -> examinationService.createExamination(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient with id 999 not found");
    }
    @Test
    void updateExamination_shouldUpdateExamination_whenExaminationExists()
    {
        Long examId = sampleExamination.getId();
        ExaminationEditDTO editDto = new ExaminationEditDTO();
        editDto.setExaminationDate(LocalDate.now().plusDays(1));
        editDto.setTreatmentDescription("Updated description");
        editDto.setSickLeaveDays(10);
        editDto.setSickLeaveStartDate(LocalDate.now().plusDays(2));
        editDto.setDoctorId(sampleDoctor.getId());
        editDto.setDiagnosisId(sampleDiagnosis.getId());


        when(examinationRepository.findByIdAndIsDeletedFalse(examId))
                .thenReturn(Optional.of(sampleExamination));

        when(doctorRepository.findByIdAndIsDeletedFalse(sampleDoctor.getId()))
                .thenReturn(Optional.of(sampleDoctor));
        when(diagnosisRepository.findById(sampleDiagnosis.getId()))
                .thenReturn(Optional.of(sampleDiagnosis));

        examinationService.updateExamination(examId, editDto);


        verify(examinationRepository, times(1)).save(sampleExamination);
        assertThat(sampleExamination.getTreatmentDescription()).isEqualTo("Updated description");
        assertThat(sampleExamination.getSickLeaveDays()).isEqualTo(10);
    }

    @Test
    void updateExamination_shouldThrow_whenExaminationNotFound()
    {
        Long nonExistentId = 999L;
        ExaminationEditDTO editDto = new ExaminationEditDTO();

        when(examinationRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> examinationService.updateExamination(nonExistentId, editDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Examination with id 999 not found");
    }

    @Test
    void deleteExamination_shouldMarkExaminationAsDeleted_whenExaminationExists() {
        // Given
        when(examinationRepository.findByIdAndIsDeletedFalse(sampleExamination.getId()))
                .thenReturn(Optional.of(sampleExamination));

        // When
        examinationService.deleteExamination(sampleExamination.getId());

        // Then
        assertThat(sampleExamination.isDeleted()).isTrue();
        verify(examinationRepository).save(sampleExamination);
    }

    @Test
    void deleteExamination_shouldThrow_whenExaminationNotFound()
    {
        Long nonExistentId = 999L;
        when(examinationRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> examinationService.deleteExamination(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Examination with id 999 not found");
    }

    @Test
    void getExaminationById_shouldReturnExaminationResponseDTO_whenFound() {
        // Given
        Long examId = sampleExamination.getId();
        when(examinationRepository.findByIdAndIsDeletedFalse(examId))
                .thenReturn(Optional.of(sampleExamination));

        // When
        ExaminationResponseDTO result = examinationService.getExaminationById(examId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleExamination.getId());
        assertThat(result.getTreatmentDescription()).isEqualTo(sampleExamination.getTreatmentDescription());
    }

    @Test
    void getExaminationById_shouldThrow_whenNotFound()
    {
        Long nonExistentId = 999L;
        when(examinationRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> examinationService.getExaminationById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Examination with id 999 not found");
    }

    @Test
    void getAllExaminations_shouldReturnListOfExaminationResponseDTO()
    {
        when(examinationRepository.findByIsDeletedFalse())
                .thenReturn(List.of(sampleExamination));

        List<ExaminationResponseDTO> result = examinationService.getAllExaminations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(sampleExamination.getId());
    }

    @Test
    void getExaminationCountsByDoctor_shouldReturnList()
    {
        DoctorExaminationCountDTO dto = new DoctorExaminationCountDTO()
        {
            @Override
            public Long getDoctorId() { return sampleDoctor.getId(); }

            public String getDoctorName() { return sampleDoctor.getFirstName() + " " + sampleDoctor.getLastName(); }
            @Override
            public Long getExaminationCount() { return 5L; }
        };
        when(examinationRepository.getExaminationCountsByDoctor())
                .thenReturn(List.of(dto));

        // When
        List<DoctorExaminationCountDTO> result = examinationService.getExaminationCountsByDoctor();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctorId()).isEqualTo(sampleDoctor.getId());
        assertThat(result.get(0).getExaminationCount()).isEqualTo(5L);
    }

    @Test
    void getExaminationsByDoctorAndDate_shouldReturnFilteredResults() {
        // Given
        Long doctorId = sampleDoctor.getId();
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(2);

        when(examinationRepository.findByDoctorIdAndExaminationDateBetweenAndIsDeletedFalse(
                eq(doctorId),
                eq(startDate),
                eq(endDate)))
                .thenReturn(List.of(sampleExamination));

        // When
        List<ExaminationResponseDTO> result =
                examinationService.getExaminationsByDoctorAndDate(doctorId, startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(sampleExamination.getId());
    }
    @Test
    void getExamsForPatient_shouldReturnPatientExams()
    {
        Long patientId = samplePatient.getId();

        when(examinationRepository.findByPatientIdAndIsDeletedFalse(patientId))
                .thenReturn(List.of(sampleExamination));

        List<ExaminationResponseDTO> result = examinationService.getExamsForPatient(patientId);


        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(sampleExamination.getId());
        assertThat(result.get(0).getTreatmentDescription())
                .isEqualTo(sampleExamination.getTreatmentDescription());
    }

    @Test
    void getExamsForPatient_shouldReturnEmptyList_whenNoExamsFound()
    {
        Long patientId = 999L;
        when(examinationRepository.findByPatientIdAndIsDeletedFalse(patientId))
                .thenReturn(Collections.emptyList());

        List<ExaminationResponseDTO> result = examinationService.getExamsForPatient(patientId);

        // Then
        assertThat(result).isEmpty();
    }
}