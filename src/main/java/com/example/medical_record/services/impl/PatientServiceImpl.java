package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.doctor.DoctorResponseDTO;
import com.example.medical_record.DTOs.examination.ExaminationResponseDTO;
import com.example.medical_record.DTOs.patient.PatientRequestDTO;
import com.example.medical_record.DTOs.patient.PatientResponseDTO;
import com.example.medical_record.DTOs.patient.PatientsWithExaminationsDTO;
import com.example.medical_record.data.entities.auth.User;
import com.example.medical_record.data.repositories.DoctorRepository;
import com.example.medical_record.data.repositories.PatientRepository;
import com.example.medical_record.data.entities.Doctor;
import com.example.medical_record.data.entities.Examination;
import com.example.medical_record.data.entities.Patient;
import com.example.medical_record.data.repositories.RoleRepository;
import com.example.medical_record.data.repositories.UserRepository;
import com.example.medical_record.exceptions.doctor.DoctorNotFoundException;
import com.example.medical_record.exceptions.patient.PatientNotFoundException;
import com.example.medical_record.services.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService
{
    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    //CREATE PATIENT
    @Override
    public void createPatient(PatientRequestDTO patientToCreate)
    {
        Patient patient = mapToEntity(patientToCreate);

        this.patientRepository.save(patient);



        User user = new User();

        user.setUsername(patientToCreate.getFirstName() + " " + patientToCreate.getLastName());

        user.setPassword(passwordEncoder.encode("1234")); // And a password

        user.setPatient(patient);

        user.addAuthority(this.roleRepository.findByAuthority("ROLE_PATIENT").orElseThrow(() -> new RuntimeException("Role 'PATIENT' not found.")));

        user.setAccountNonExpired(true);

        user.setAccountNonLocked(true);

        user.setCredentialsNonExpired(true);

        user.setEnabled(true);

        this.userRepository.save(user);
    }

    //UPDATE PATIENT
    @Override
    public void updatePatient(Long id, PatientRequestDTO patientToUpdate) throws PatientNotFoundException, DoctorNotFoundException
    {
        Patient existingPatient = this.patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found."));

        existingPatient.setFirstName(patientToUpdate.getFirstName());

        existingPatient.setLastName(patientToUpdate.getLastName());

        existingPatient.setHealthInsurancePaid(patientToUpdate.isHealthInsurancePaid());

        if (patientToUpdate.getPersonalDoctorId() != null)
        {
            Doctor personalDoctor = this.doctorRepository.findById(patientToUpdate.getPersonalDoctorId())
                    .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + patientToUpdate.getPersonalDoctorId() + " not found."));

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
    public void deletePatient(Long id) throws PatientNotFoundException
    {
        Patient patient = this.patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found."));

        patient.setDeleted(true);

        this.patientRepository.save(patient);
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) throws PatientNotFoundException
    {
        Patient patient = this.patientRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found."));

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

    //GET ALL PATIENTS WITH SPECIFIC ID
    @Override
    public List<PatientResponseDTO> getAllPatientsWithSameDiagnosis(Long diagnosisId)
    {
        List<Patient> patients = this.patientRepository.findDistinctByExaminationsDiagnosisIdAndIsDeletedFalse(diagnosisId);

        return patients.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    //GET ALL PATIENTS WITH SAME DOCTOR
    @Override
    public List<PatientResponseDTO> getPatientsByDoctorId(Long doctorId)
    {
        List<Patient> patients = this.patientRepository.findByPersonalDoctorIdAndIsDeletedFalse(doctorId);

        return patients.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientsWithExaminationsDTO> getAllPatientsWithExaminations()
    {
        return this.patientRepository.findByIsDeletedFalse().stream()
                .map(patient -> {
                    List<ExaminationResponseDTO> examinationDTOs = patient.getExaminations().stream()
                            .filter(exam -> !exam.isDeleted()) // Exclude deleted examinations
                            .map(this::mapToExaminationResponseDTO)
                            .collect(Collectors.toList());

                    return new PatientsWithExaminationsDTO(mapToResponseDTO(patient), examinationDTOs);
                })
                .collect(Collectors.toList());
    }

    //Helper method for mapping examination to dto
    private ExaminationResponseDTO mapToExaminationResponseDTO(Examination examination)
    {
        ExaminationResponseDTO dto = new ExaminationResponseDTO();

        dto.setId(examination.getId());

        dto.setExaminationDate(examination.getExaminationDate());

        dto.setTreatmentDescription(examination.getTreatmentDescription());

        dto.setSickLeaveDays(examination.getSickLeaveDays());

        dto.setSickLeaveStartDate(examination.getSickLeaveStartDate());

        return dto;
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
