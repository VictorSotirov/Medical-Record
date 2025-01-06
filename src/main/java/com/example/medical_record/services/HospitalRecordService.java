package com.example.medical_record.services;


import com.example.medical_record.DTOs.doctor.DoctorWithMostRecordsDTO;
import com.example.medical_record.DTOs.hospitalRecord.*;
import com.example.medical_record.services.impl.MonthWithHospitalRecordsDTO;

import java.time.LocalDate;
import java.util.List;

public interface HospitalRecordService
{
    void createHospitalRecord(HospitalRecordRequestDTO patientToCreate);

    void updateHospitalRecord(Long id, HospitalRecordEditDTO hospitalRecordToUpdate);

    void deleteHospitalRecord(Long id);

    HospitalRecordResponseDTO getHospitalRecordById(Long id);

    List<HospitalRecordResponseDTO> getAllHospitalRecords();

    //GET ALL HOSPITAL RECORDS IN A CERTAIN TIMEFRAME
    List<HospitalRecordResponseDTO> getHospitalRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    MonthWithHospitalRecordsDTO getMonthWithMostHospitalRecords();

    DoctorWithMostRecordsDTO getDoctorWithMostRecords();
}
