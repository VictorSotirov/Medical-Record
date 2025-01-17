package com.example.medical_record.DTOs.patient;

import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientsWithExaminationsDTO
{
    private PatientResponseDTO patient;

    private List<ExaminationResponseDTO> examinations;
}
