package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.doctor.DoctorRequestDTO;
import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.data.DoctorRepository;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.services.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorServiceImpl implements DoctorService
{
    private final DoctorRepository doctorRepository;

    //CREATE DOCTOR
    @Override
    public void createDoctor(DoctorRequestDTO doctorToCreate)
    {
        Doctor doctor = mapToEntity(doctorToCreate);

        this.doctorRepository.save(doctor);
    }

    //UPDATE DOCTOR
    @Override
    public void updateDoctor(Long id, DoctorRequestDTO updatedDoctor)
    {
        Doctor existingDoctor = this.doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor with id: " + id + " not found."));

        existingDoctor.setFirstName(updatedDoctor.getFirstName());

        existingDoctor.setLastName(updatedDoctor.getLastName());

        existingDoctor.setSpecialty(updatedDoctor.getSpecialty());

        this.doctorRepository.save(existingDoctor);

    }

    //DELETE DOCTOR
    @Override
    public void deleteDoctor(Long id)
    {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor with id " + id + " not found."));

        doctor.setDeleted(true);

        doctorRepository.save(doctor);
    }

    //GET DOCTOR BY ID
    @Override
    public DoctorResponseDTO getDoctorById(Long id)
    {
        Doctor doctor = doctorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Doctor with id " + id + " not found."));

        return mapToResponseDTO(doctor);
    }

    //GET ALL DOCTORS
    @Override
    public List<DoctorResponseDTO> getAllDoctors()
    {
        List<Doctor> activeDoctors = doctorRepository.findByIsDeletedFalse();

        return activeDoctors.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // Helper method to map entity to DTO
    private DoctorResponseDTO mapToResponseDTO(Doctor doctor)
    {
        DoctorResponseDTO dto = new DoctorResponseDTO();

        dto.setId(doctor.getId());

        dto.setFirstName(doctor.getFirstName());

        dto.setLastName(doctor.getLastName());

        dto.setSpecialty(doctor.getSpecialty());

        return dto;
    }

    // Helper method to map DTO to entity
    private Doctor mapToEntity(DoctorRequestDTO dto)
    {
        Doctor doctor = new Doctor();

        doctor.setFirstName(dto.getFirstName());

        doctor.setLastName(dto.getLastName());

        doctor.setSpecialty(dto.getSpecialty());

        return doctor;
    }
}
