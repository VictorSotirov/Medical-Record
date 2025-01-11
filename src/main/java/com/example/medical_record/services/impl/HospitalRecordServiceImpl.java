package com.example.medical_record.services.impl;


import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.doctor.DoctorWithMostRecordsDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordEditDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordRequestDTO;
import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordResponseDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.HospitalRecordRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.HospitalRecord;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.services.HospitalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HospitalRecordServiceImpl implements HospitalRecordService
{
    private final HospitalRecordRepository hospitalRecordRepository;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    //CREATE HOSPITAL RECORD
    @Override
    public void createHospitalRecord(HospitalRecordRequestDTO hospitalRecordToCreate)
    {
        HospitalRecord hospitalRecord = mapToEntity(hospitalRecordToCreate);

        this.hospitalRecordRepository.save(hospitalRecord);
    }

    //UPDATE HOSPITAL RECORD
    @Override
    public void updateHospitalRecord(Long id, HospitalRecordEditDTO hospitalRecordToUpdate)
    {
        HospitalRecord existingRecord = this.hospitalRecordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Hospital record with id " + id + " not found."));

        existingRecord.setAdmissionDate(hospitalRecordToUpdate.getAdmissionDate());

        existingRecord.setDischargeDate(hospitalRecordToUpdate.getDischargeDate());

        //TODO REFACTOR
        //if (id.get != null)
        //{
            Patient patient = this.patientRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Patient with id " + id + " not found."));

            existingRecord.setPatient(patient);
        //}

        if (hospitalRecordToUpdate.getDoctorId() != null)
        {
            Doctor doctor = this.doctorRepository.findByIdAndIsDeletedFalse(hospitalRecordToUpdate.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor with id " + hospitalRecordToUpdate.getDoctorId() + " not found."));

            existingRecord.setDoctor(doctor);
        }

        this.hospitalRecordRepository.save(existingRecord);
    }

    //DELETE HOSPITAL RECORD
    @Override
    public void deleteHospitalRecord(Long id)
    {
        HospitalRecord hospitalRecord = this.hospitalRecordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Hospital record with id " + id + " not found."));

        hospitalRecord.setDeleted(true);

        this.hospitalRecordRepository.save(hospitalRecord);
    }

    //GET HOSPITAL RECORD BY ID
    @Override
    public HospitalRecordResponseDTO getHospitalRecordById(Long id)
    {
        HospitalRecord hospitalRecord = this.hospitalRecordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Hospital record with id " + id + " not found."));

        return mapToResponseDTO(hospitalRecord);
    }

    //GET ALL HOSPITAL RECORDS
    @Override
    public List<HospitalRecordResponseDTO> getAllHospitalRecords()
    {
        List<HospitalRecord> activeRecords = this.hospitalRecordRepository.findByIsDeletedFalse();

        return activeRecords.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HospitalRecordResponseDTO> getHospitalRecordsByDateRange(LocalDate startDate, LocalDate endDate)
    {
        List<HospitalRecord> recordsInTimePeriod = this.hospitalRecordRepository.findByAdmissionDateBetweenAndIsDeletedFalse(startDate, endDate);

        return recordsInTimePeriod.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MonthWithHospitalRecordsDTO getMonthWithMostHospitalRecords()
    {
        List<Object[]> result = this.hospitalRecordRepository.findMonthWithMostHospitalRecords();

        if (result.isEmpty())
        {
            throw new RuntimeException("No hospital records found for the current year.");
        }

        Object[] mostRecordsData = result.get(0);

        int month = (int) mostRecordsData[0];

        long count = (long) mostRecordsData[1];

        List<HospitalRecord> records = hospitalRecordRepository.findRecordsByMonth(month);

        List<HospitalRecordResponseDTO> recordDTOs = records.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return new MonthWithHospitalRecordsDTO(month, count, recordDTOs);
    }

    @Override
    public DoctorWithMostRecordsDTO getDoctorWithMostRecords()
    {
        List<Object[]> results = hospitalRecordRepository.findDoctorWithMostRecords();

        if (results.isEmpty()) {
            throw new RuntimeException("No doctor records found.");
        }

        // Get the first result
        Object[] result = results.get(0);

        Long doctorId = ((Number) result[0]).longValue(); // Cast to Number first, then to Long

        String firstName = (String) result[1];

        String lastName = (String) result[2];

        String specialty = (String) result[3];

        Long recordCount = ((Number) result[4]).longValue(); // Cast to Number first, then to Long

        return new DoctorWithMostRecordsDTO(doctorId, firstName, lastName, specialty, recordCount);
    }



    // Helper method to map entity to DTO
    private HospitalRecordResponseDTO mapToResponseDTO(HospitalRecord hospitalRecord)
    {
        HospitalRecordResponseDTO responseDTO = new HospitalRecordResponseDTO();

        responseDTO.setId(hospitalRecord.getId());

        responseDTO.setAdmissionDate(hospitalRecord.getAdmissionDate());

        responseDTO.setDischargeDate(hospitalRecord.getDischargeDate());

        // Map patient to PatientResponseDTO
        PatientResponseDTO patientDTO = new PatientResponseDTO();

        patientDTO.setId(hospitalRecord.getPatient().getId());

        patientDTO.setFirstName(hospitalRecord.getPatient().getFirstName());

        patientDTO.setLastName(hospitalRecord.getPatient().getLastName());

        patientDTO.setHealthInsurancePaid(hospitalRecord.getPatient().isHealthInsurancePaid());

        responseDTO.setPatient(patientDTO);

        // Map doctor to DoctorResponseDTO
        DoctorResponseDTO doctorDTO = new DoctorResponseDTO();

        doctorDTO.setId(hospitalRecord.getDoctor().getId());

        doctorDTO.setFirstName(hospitalRecord.getDoctor().getFirstName());

        doctorDTO.setLastName(hospitalRecord.getDoctor().getLastName());

        doctorDTO.setSpecialty(hospitalRecord.getDoctor().getSpecialty());

        responseDTO.setDoctor(doctorDTO);

        return responseDTO;
    }

    // Helper method to map DTO to entity
    private HospitalRecord mapToEntity(HospitalRecordRequestDTO hospitalRecordRequestDTO)
    {
        HospitalRecord hospitalRecord = new HospitalRecord();

        hospitalRecord.setAdmissionDate(hospitalRecordRequestDTO.getAdmissionDate());

        hospitalRecord.setDischargeDate(hospitalRecordRequestDTO.getDischargeDate());

        // Map patient ID to Patient entity
        Patient patient = this.patientRepository.findById(hospitalRecordRequestDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient with id " + hospitalRecordRequestDTO.getPatientId() + " not found."));
        hospitalRecord.setPatient(patient);

        // Map doctor ID to Doctor entity
        Doctor doctor = this.doctorRepository.findById(hospitalRecordRequestDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor with id " + hospitalRecordRequestDTO.getDoctorId() + " not found."));
        hospitalRecord.setDoctor(doctor);

        return hospitalRecord;
    }
}
