package com.example.medical_record.services.impl;


import com.example.medical_record.DTOs.doctor.DoctorWithMostRecordsDTO;
import com.example.medical_record.DTOs.hospitalRecord.*;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.HospitalRecord;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.HospitalRecordRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalRecordServiceImplTest {

    @Mock
    private HospitalRecordRepository hospitalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private HospitalRecordServiceImpl hospitalRecordService;

    private Patient samplePatient;
    private Doctor sampleDoctor;
    private HospitalRecord sampleRecord;

    @BeforeEach
    void setUp()
    {
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .isDeleted(false)
                .isHealthInsurancePaid(true)
                .build();

        sampleDoctor = Doctor.builder()
                .id(2L)
                .firstName("Dr. Jane")
                .lastName("Smith")
                .specialty("General")
                .isDeleted(false)
                .build();

        sampleRecord = HospitalRecord.builder()
                .id(100L)
                .admissionDate(LocalDate.of(2025, 1, 10))
                .dischargeDate(LocalDate.of(2025, 1, 15))
                .patient(samplePatient)
                .doctor(sampleDoctor)
                .isDeleted(false)
                .build();
    }

    @Test
    void createHospitalRecord_shouldSaveRecord_whenPatientAndDoctorExist()
    {
        HospitalRecordRequestDTO requestDTO = new HospitalRecordRequestDTO();
        requestDTO.setAdmissionDate(LocalDate.of(2025, 1, 10));
        requestDTO.setDischargeDate(LocalDate.of(2025, 1, 15));
        requestDTO.setPatientId(samplePatient.getId());
        requestDTO.setDoctorId(sampleDoctor.getId());

        when(patientRepository.findByIdAndIsDeletedFalse(samplePatient.getId()))
                .thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findByIdAndIsDeletedFalse(sampleDoctor.getId()))
                .thenReturn(Optional.of(sampleDoctor));
        when(hospitalRecordRepository.save(any(HospitalRecord.class)))
                .thenReturn(sampleRecord);


        hospitalRecordService.createHospitalRecord(requestDTO);


        verify(hospitalRecordRepository).save(any(HospitalRecord.class));
    }

    @Test
    void createHospitalRecord_shouldThrow_whenPatientNotFound()
    {
        HospitalRecordRequestDTO requestDTO = new HospitalRecordRequestDTO();
        requestDTO.setAdmissionDate(LocalDate.of(2025, 1, 10));
        requestDTO.setDischargeDate(LocalDate.of(2025, 1, 15));
        requestDTO.setPatientId(999L);
        requestDTO.setDoctorId(sampleDoctor.getId());

        when(patientRepository.findByIdAndIsDeletedFalse(999L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> hospitalRecordService.createHospitalRecord(requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient with id 999 not found");
    }

    @Test
    void createHospitalRecord_shouldThrow_whenDoctorNotFound()
    {
        HospitalRecordRequestDTO requestDTO = new HospitalRecordRequestDTO();
        requestDTO.setAdmissionDate(LocalDate.of(2025, 1, 10));
        requestDTO.setDischargeDate(LocalDate.of(2025, 1, 15));
        requestDTO.setPatientId(samplePatient.getId());
        requestDTO.setDoctorId(999L);

        when(patientRepository.findByIdAndIsDeletedFalse(samplePatient.getId()))
                .thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findByIdAndIsDeletedFalse(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalRecordService.createHospitalRecord(requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor with id 999 not found");
    }
    @Test
    void updateHospitalRecord_shouldUpdate_whenRecordPatientAndDoctorFound()
    {
        Long recordId = sampleRecord.getId();
        HospitalRecordEditDTO editDTO = new HospitalRecordEditDTO();
        editDTO.setAdmissionDate(LocalDate.of(2025, 2, 1));
        editDTO.setDischargeDate(LocalDate.of(2025, 2, 5));
        editDTO.setDoctorId(sampleDoctor.getId());

        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.of(sampleRecord));

        when(patientRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.of(samplePatient));

        when(doctorRepository.findByIdAndIsDeletedFalse(sampleDoctor.getId()))
                .thenReturn(Optional.of(sampleDoctor));

        hospitalRecordService.updateHospitalRecord(recordId, editDTO);


        verify(hospitalRecordRepository).save(sampleRecord);
        assertThat(sampleRecord.getAdmissionDate()).isEqualTo(editDTO.getAdmissionDate());
        assertThat(sampleRecord.getDischargeDate()).isEqualTo(editDTO.getDischargeDate());
        assertThat(sampleRecord.getPatient()).isEqualTo(samplePatient);
        assertThat(sampleRecord.getDoctor()).isEqualTo(sampleDoctor);
    }

    @Test
    void updateHospitalRecord_shouldThrow_whenRecordNotFound()
    {
        Long nonExistentId = 999L;
        HospitalRecordEditDTO editDTO = new HospitalRecordEditDTO();
        editDTO.setAdmissionDate(LocalDate.of(2025, 2, 1));
        editDTO.setDischargeDate(LocalDate.of(2025, 2, 5));
        editDTO.setDoctorId(sampleDoctor.getId());

        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalRecordService.updateHospitalRecord(nonExistentId, editDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Hospital record with id 999 not found");
    }

    @Test
    void updateHospitalRecord_shouldThrow_whenPatientNotFound()
    {
        Long recordId = sampleRecord.getId();
        HospitalRecordEditDTO editDTO = new HospitalRecordEditDTO();
        editDTO.setAdmissionDate(LocalDate.of(2025, 2, 1));
        editDTO.setDischargeDate(LocalDate.of(2025, 2, 5));
        editDTO.setDoctorId(sampleDoctor.getId());

        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.of(sampleRecord));

        when(patientRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalRecordService.updateHospitalRecord(recordId, editDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient with id 100 not found");
    }

    @Test
    void updateHospitalRecord_shouldThrow_whenDoctorNotFound()
    {
        Long recordId = sampleRecord.getId();
        HospitalRecordEditDTO editDTO = new HospitalRecordEditDTO();
        editDTO.setAdmissionDate(LocalDate.of(2025, 2, 1));
        editDTO.setDischargeDate(LocalDate.of(2025, 2, 5));
        editDTO.setDoctorId(999L);

        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.of(sampleRecord));
        when(patientRepository.findByIdAndIsDeletedFalse(recordId))
                .thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findByIdAndIsDeletedFalse(999L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> hospitalRecordService.updateHospitalRecord(recordId, editDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor with id 999 not found");
    }

    @Test
    void deleteHospitalRecord_shouldMarkDeleted_whenRecordFound()
    {
        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(sampleRecord.getId()))
                .thenReturn(Optional.of(sampleRecord));

        hospitalRecordService.deleteHospitalRecord(sampleRecord.getId());

        verify(hospitalRecordRepository).save(sampleRecord);
        assertThat(sampleRecord.isDeleted()).isTrue();
    }

    @Test
    void deleteHospitalRecord_shouldThrow_whenNotFound()
    {
        Long nonExistentId = 999L;
        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalRecordService.deleteHospitalRecord(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Hospital record with id 999 not found");
    }

    @Test
    void getHospitalRecordById_shouldReturnResponseDTO_whenFound()
    {
        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(sampleRecord.getId()))
                .thenReturn(Optional.of(sampleRecord));

        HospitalRecordResponseDTO result =
                hospitalRecordService.getHospitalRecordById(sampleRecord.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleRecord.getId());
        assertThat(result.getPatient().getId()).isEqualTo(samplePatient.getId());
        assertThat(result.getDoctor().getId()).isEqualTo(sampleDoctor.getId());
    }

    @Test
    void getHospitalRecordById_shouldThrow_whenNotFound()
    {
        Long nonExistentId = 999L;
        when(hospitalRecordRepository.findByIdAndIsDeletedFalse(nonExistentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalRecordService.getHospitalRecordById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Hospital record with id 999 not found");
    }

    @Test
    void getAllHospitalRecords_shouldReturnListOfResponseDTO()
    {
        when(hospitalRecordRepository.findByIsDeletedFalse())
                .thenReturn(List.of(sampleRecord));

        List<HospitalRecordResponseDTO> results = hospitalRecordService.getAllHospitalRecords();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(sampleRecord.getId());
    }

    @Test
    void getHospitalRecordsByDateRange_shouldReturnList()
    {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        when(hospitalRecordRepository.findByAdmissionDateBetweenAndIsDeletedFalse(startDate, endDate))
                .thenReturn(List.of(sampleRecord));

        List<HospitalRecordResponseDTO> results =
                hospitalRecordService.getHospitalRecordsByDateRange(startDate, endDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(sampleRecord.getId());
    }

    @Test
    void getMonthWithMostHospitalRecords_shouldReturnDTO_whenDataExists()
    {
        // Option A: Assign the data to a local variable
        List<Object[]> data = Collections.singletonList(new Object[]{5, 10L});

        when(hospitalRecordRepository.findMonthWithMostHospitalRecords())
                .thenReturn(data);

        MonthWithHospitalRecordsDTO result = hospitalRecordService.getMonthWithMostHospitalRecords();

        // Assertions
        assertThat(result.getMonth()).isEqualTo(5);
        assertThat(result.getRecordCount()).isEqualTo(10L);
    }

    @Test
    void getMonthWithMostHospitalRecords_shouldThrow_whenNoRecordsInCurrentYear() {
        // Given
        when(hospitalRecordRepository.findMonthWithMostHospitalRecords())
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> hospitalRecordService.getMonthWithMostHospitalRecords())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hospital records found for the current year.");
    }

    @Test
    void getDoctorWithMostRecords_shouldReturnDTO_whenDataExists()
    {
        List<Object[]> data = Collections.singletonList(
                new Object[] {2L, "Dr. Jane", "Smith", "General", 5L}
        );

        when(hospitalRecordRepository.findDoctorWithMostRecords())
                .thenReturn(data);


        DoctorWithMostRecordsDTO result = hospitalRecordService.getDoctorWithMostRecords();

        assertThat(result.getDoctorId()).isEqualTo(2L);
        assertThat(result.getFirstName()).isEqualTo("Dr. Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getSpecialty()).isEqualTo("General");
        assertThat(result.getRecordCount()).isEqualTo(5L);
    }

    @Test
    void getDoctorWithMostRecords_shouldThrow_whenNoRecordsFound()
    {
        when(hospitalRecordRepository.findDoctorWithMostRecords())
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> hospitalRecordService.getDoctorWithMostRecords())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No doctor records found.");
    }
}
