package com.example.medical_record.services;

import com.example.medical_record.DTOs.doctor.*;

import java.util.List;

public interface DoctorService
{
    void createDoctor(DoctorRequestDTO doctor);

    void updateDoctor(Long id, DoctorRequestDTO doctorToUpdate);

    void deleteDoctor(Long id);

    DoctorResponseDTO getDoctorById(Long id);

    List<DoctorResponseDTO> getAllDoctors();

    List<DoctorWithPatientsDTO> getAllDoctorsWithPatients();
}
