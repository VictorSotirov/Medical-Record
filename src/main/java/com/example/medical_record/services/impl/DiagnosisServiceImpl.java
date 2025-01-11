package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.diagnosis.DiagnosisFrequencyDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.exceptions.diagnosis.DiagnosisAlreadyExistsException;
import com.example.medical_record.exceptions.diagnosis.DiagnosisNotFoundException;
import com.example.medical_record.services.DiagnosisService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService
{
    private final DiagnosisRepository diagnosisRepository;

    //CREATE DIAGNOSIS
    @Override
    public void createDiagnosis(DiagnosisRequestDTO diagnosisToCreate) throws DiagnosisAlreadyExistsException
    {
        //CHECK IF DIAGNOSIS WITH SAME DESCRIPTION ALREADY EXISTS
        if (this.diagnosisRepository.findByDescription(diagnosisToCreate.getDescription()).isPresent())
        {
            throw new DiagnosisAlreadyExistsException("Diagnosis already exists");
        }

        Diagnosis diagnosis = mapToEntity(diagnosisToCreate);

        this.diagnosisRepository.save(diagnosis);
    }

    //UPDATE DIAGNOSIS
    @Override
    public void updateDiagnosis(Long id, DiagnosisRequestDTO updatedDiagnosis) throws DiagnosisAlreadyExistsException, DiagnosisNotFoundException
    {
        Diagnosis existingDiagnosis = this.diagnosisRepository.findById(id)
                .orElseThrow(() -> new DiagnosisNotFoundException("Diagnosis with id: " + id + " not found."));

        Optional<Diagnosis> diagnosisByDescription = this.diagnosisRepository.findByDescription(updatedDiagnosis.getDescription());

        if (diagnosisByDescription.isPresent())
        {
            //CHECK TO SEE IF USER HAS NOT MADE ANY CHANGES OR IS TRYING TO CHANGE TO EXISTING DIAGNOSIS
            if (!diagnosisByDescription.get().getId().equals(id))
            {
                throw new DiagnosisAlreadyExistsException("Diagnosis already exists");
            }
        }

        existingDiagnosis.setDescription(updatedDiagnosis.getDescription());

        this.diagnosisRepository.save(existingDiagnosis);
    }

    //DELETE DIAGNOSIS
    // Potentially REFACTOR FOR SOFT DELETE
    @Override
    public void deleteDiagnosis(Long id) throws DiagnosisNotFoundException
    {
        Diagnosis diagnosis = this.diagnosisRepository.findById(id)
                .orElseThrow(() -> new DiagnosisNotFoundException("Diagnosis with id: " + id + " not found."));

        this.diagnosisRepository.delete(diagnosis);
    }

    //GET DIAGNOSIS BY ID
    @Override
    public DiagnosisResponseDTO getDiagnosisById(Long id) throws DiagnosisNotFoundException
    {
        Diagnosis diagnosis = this.diagnosisRepository.findById(id)
                .orElseThrow(() -> new DiagnosisNotFoundException("Diagnosis with id: " + id + " not found."));

        return mapToResponseDTO(diagnosis);
    }

    //GET ALL DIAGNOSES
    @Override
    public List<DiagnosisResponseDTO> getAllDiagnoses()
    {
        return this.diagnosisRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    //GET MOST COMMON DIAGNOSES
    @Override
    public List<DiagnosisFrequencyDTO> getMostCommonDiagnoses()
    {
        List<Object[]> results = diagnosisRepository.findMostCommonDiagnoses();
        return results.stream()
                .map(result ->
                {
                    Diagnosis diagnosis = (Diagnosis) result[0];
                    Long frequency = (Long) result[1];
                    DiagnosisResponseDTO diagnosisDTO = mapToResponseDTO(diagnosis);
                    return new DiagnosisFrequencyDTO(diagnosisDTO, frequency);
                })
                .collect(Collectors.toList());
    }


    // Helper method to map entity to DTO
    private DiagnosisResponseDTO mapToResponseDTO(Diagnosis diagnosis) {
        DiagnosisResponseDTO dto = new DiagnosisResponseDTO();
        dto.setId(diagnosis.getId());
        dto.setDescription(diagnosis.getDescription());
        return dto;
    }

    // Helper method to map DTO to entity
    private Diagnosis mapToEntity(DiagnosisRequestDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDescription(dto.getDescription());
        return diagnosis;
    }
}
