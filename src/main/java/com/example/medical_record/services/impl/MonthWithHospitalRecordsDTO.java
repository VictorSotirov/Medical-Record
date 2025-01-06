package com.example.medical_record.services.impl;


import com.example.medical_record.DTOs.hospitalRecord.HospitalRecordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MonthWithHospitalRecordsDTO
{
    private int month;

    private long recordCount;

    private List<HospitalRecordResponseDTO> records;
}
