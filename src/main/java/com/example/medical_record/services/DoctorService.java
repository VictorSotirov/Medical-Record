package com.example.medical_record.services;

import com.example.medical_record.DTOs.doctor.*;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;

import java.util.List;

public interface DoctorService
{
    void createDoctor(DoctorRequestDTO doctor);

    void updateDoctor(Long id, DoctorRequestDTO doctorToUpdate) throws DoctorNotFoundException;

    void deleteDoctor(Long id) throws DoctorNotFoundException;

    DoctorResponseDTO getDoctorById(Long id) throws DoctorNotFoundException;

    List<DoctorResponseDTO> getAllDoctors();

    List<DoctorWithPatientsDTO> getAllDoctorsWithPatients();
}
