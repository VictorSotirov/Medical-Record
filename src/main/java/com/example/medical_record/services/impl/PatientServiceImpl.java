package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.patient.PatientRequestDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.data.DoctorRepository;
import com.example.medical_record.data.PatientRepository;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService
{
    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;


    //CREATE PATIENT
    @Override
    public void createPatient(PatientRequestDTO patientToCreate)
    {
        Patient patient = mapToEntity(patientToCreate);

        this.patientRepository.save(patient);
    }

    //UPDATE PATIENT
    @Override
    public void updatePatient(Long id, PatientRequestDTO patientToUpdate)
    {
        Patient existingPatient = this.patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient with id " + id + " not found."));

        existingPatient.setFirstName(patientToUpdate.getFirstName());

        existingPatient.setLastName(patientToUpdate.getLastName());

        existingPatient.setHealthInsurancePaid(patientToUpdate.isHealthInsurancePaid());

        if (patientToUpdate.getPersonalDoctorId() != null)
        {
            Doctor personalDoctor = this.doctorRepository.findById(patientToUpdate.getPersonalDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor with id " + patientToUpdate.getPersonalDoctorId() + " not found."));

            existingPatient.setPersonalDoctor(personalDoctor);
        }
        else
        {
            existingPatient.setPersonalDoctor(null); // Remove doctor if ID is not provided
        }

        this.patientRepository.save(existingPatient);
    }

    //DELETE PATIENT
    @Override
    public void deletePatient(Long id)
    {
        Patient patient = this.patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient with id " + id + " not found."));

        patient.setDeleted(true);

        this.patientRepository.save(patient);
    }

    @Override
    public PatientResponseDTO getPatientById(Long id)
    {
        Patient patient = this.patientRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Patient with id " + id + " not found."));

        return mapToResponseDTO(patient);
    }

    @Override
    public List<PatientResponseDTO> getAllPatients()
    {
        List<Patient> activePatients = this.patientRepository.findByIsDeletedFalse();

        return activePatients.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method to map entity to DTO
    public PatientResponseDTO mapToResponseDTO(Patient patient)
    {
        PatientResponseDTO responseDTO = new PatientResponseDTO();

        responseDTO.setId(patient.getId());

        responseDTO.setFirstName(patient.getFirstName());

        responseDTO.setLastName(patient.getLastName());

        responseDTO.setHealthInsurancePaid(patient.isHealthInsurancePaid());

        if (patient.getPersonalDoctor() != null)
        {
            DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();

            doctorResponseDTO.setId(patient.getPersonalDoctor().getId());

            doctorResponseDTO.setFirstName(patient.getPersonalDoctor().getFirstName());

            doctorResponseDTO.setLastName(patient.getPersonalDoctor().getLastName());

            doctorResponseDTO.setSpecialty(patient.getPersonalDoctor().getSpecialty());

            responseDTO.setPersonalDoctor(doctorResponseDTO);

            //responseDTO.setPersonalDoctorId(doctorResponseDTO.getId());
        }

        return responseDTO;
    }

    // Helper method to map DTO to entity
    public Patient mapToEntity(PatientRequestDTO patientRequestDTO)
    {
        Patient patient = new Patient();

        patient.setFirstName(patientRequestDTO.getFirstName());

        patient.setLastName(patientRequestDTO.getLastName());

        patient.setHealthInsurancePaid(patientRequestDTO.isHealthInsurancePaid());

        if (patientRequestDTO.getPersonalDoctorId() != null)
        {
            Doctor personalDoctor = this.doctorRepository.findById(patientRequestDTO.getPersonalDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor with id " + patientRequestDTO.getPersonalDoctorId() + " not found."));

            patient.setPersonalDoctor(personalDoctor);
        }

        return patient;
    }
}
