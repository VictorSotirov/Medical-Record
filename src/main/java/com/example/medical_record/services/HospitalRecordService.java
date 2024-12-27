package com.example.medical_record.services;


import com.example.medical_record.DTOs.hospitalRecord.*;

import java.util.List;

public interface HospitalRecordService
{
    void createHospitalRecord(HospitalRecordRequestDTO patientToCreate);

    void updateHospitalRecord(Long id, HospitalRecordEditDTO hospitalRecordToUpdate);

    void deleteHospitalRecord(Long id);

    HospitalRecordResponseDTO getHospitalRecordById(Long id);

    List<HospitalRecordResponseDTO> getAllHospitalRecords();
}
