package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.diagnosis.DiagnosisFrequencyDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisRequestDTO;
import com.example.medical_record.DTOs.diagnosis.DiagnosisResponseDTO;
import com.example.medical_record.data.entities.Diagnosis;
import com.example.medical_record.data.repositories.DiagnosisRepository;
import com.example.medical_record.exceptions.diagnosis.DiagnosisAlreadyExistsException;
import com.example.medical_record.exceptions.diagnosis.DiagnosisNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceImplTest
{
    @Mock
    private DiagnosisRepository diagnosisRepository;

    @InjectMocks
    private DiagnosisServiceImpl diagnosisService;

    @Test
    void createDiagnosis_ShouldCreateDiagnosis_WhenDiagnosisDoesNotExist() throws DiagnosisAlreadyExistsException
    {
        DiagnosisRequestDTO requestDTO = new DiagnosisRequestDTO();

        requestDTO.setDescription("New Diagnosis");

        when(diagnosisRepository.findByDescription(requestDTO.getDescription())).thenReturn(Optional.empty());

        diagnosisService.createDiagnosis(requestDTO);

        verify(diagnosisRepository, times(1)).save(any(Diagnosis.class));
    }

    @Test
    void createDiagnosis_ShouldThrowException_WhenDiagnosisExists()
    {
        DiagnosisRequestDTO requestDTO = new DiagnosisRequestDTO();

        requestDTO.setDescription("Existing Diagnosis");

        when(diagnosisRepository.findByDescription(requestDTO.getDescription()))
                .thenReturn(Optional.of(new Diagnosis(1L, "Existing Diagnosis", null)));

        assertThrows(DiagnosisAlreadyExistsException.class, () -> diagnosisService.createDiagnosis(requestDTO));

        verify(diagnosisRepository, never()).save(any(Diagnosis.class));
    }

    @Test
    void updateDiagnosis_ShouldUpdateDiagnosis_WhenValidIdAndNoConflict() throws DiagnosisAlreadyExistsException, DiagnosisNotFoundException
    {
        Long id = 1L;

        Diagnosis existingDiagnosis = new Diagnosis(id, "Old Diagnosis", null);

        DiagnosisRequestDTO updatedDTO = new DiagnosisRequestDTO();

        updatedDTO.setDescription("Updated Diagnosis");

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(existingDiagnosis));
        when(diagnosisRepository.findByDescription(updatedDTO.getDescription())).thenReturn(Optional.empty());

        diagnosisService.updateDiagnosis(id, updatedDTO);

        assertEquals("Updated Diagnosis", existingDiagnosis.getDescription());
        verify(diagnosisRepository, times(1)).save(existingDiagnosis);
    }

    @Test
    void updateDiagnosis_ShouldThrowException_WhenDiagnosisWithSameDescriptionExists()
    {
        Long id = 1L;
        Diagnosis existingDiagnosis = new Diagnosis(id, "Old Diagnosis", null);

        DiagnosisRequestDTO updatedDTO = new DiagnosisRequestDTO();

        updatedDTO.setDescription("Existing Diagnosis");

        Diagnosis otherDiagnosis = new Diagnosis(2L, "Existing Diagnosis", null);

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(existingDiagnosis));
        when(diagnosisRepository.findByDescription(updatedDTO.getDescription())).thenReturn(Optional.of(otherDiagnosis));

        assertThrows(DiagnosisAlreadyExistsException.class, () -> diagnosisService.updateDiagnosis(id, updatedDTO));

        verify(diagnosisRepository, never()).save(any());
    }

    @Test
    void deleteDiagnosis_ShouldDeleteDiagnosis_WhenExists() throws DiagnosisNotFoundException
    {
        Long id = 1L;
        Diagnosis existingDiagnosis = new Diagnosis(id, "Diagnosis to Delete", null);

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(existingDiagnosis));

        diagnosisService.deleteDiagnosis(id);

        verify(diagnosisRepository, times(1)).delete(existingDiagnosis);
    }

    @Test
    void deleteDiagnosis_ShouldThrowException_WhenNotFound()
    {
        Long id = 1L;

        when(diagnosisRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DiagnosisNotFoundException.class, () -> diagnosisService.deleteDiagnosis(id));

        verify(diagnosisRepository, never()).delete(any());
    }

    @Test
    void getDiagnosisById_ShouldReturnDiagnosis_WhenExists() throws DiagnosisNotFoundException
    {
        Long id = 1L;
        Diagnosis existingDiagnosis = new Diagnosis(id, "Existing Diagnosis", null);

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(existingDiagnosis));

        DiagnosisResponseDTO result = diagnosisService.getDiagnosisById(id);

        assertEquals(id, result.getId());
        assertEquals("Existing Diagnosis", result.getDescription());
    }

    @Test
    void getDiagnosisById_ShouldThrowException_WhenNotFound()
    {
        Long id = 1L;

        when(diagnosisRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DiagnosisNotFoundException.class, () -> diagnosisService.getDiagnosisById(id));
    }

    @Test
    void getAllDiagnoses_ShouldReturnListOfDiagnoses()
    {
        List<Diagnosis> diagnoses = List.of(
                new Diagnosis(1L, "Diagnosis 1", null),
                new Diagnosis(2L, "Diagnosis 2", null)
        );

        when(diagnosisRepository.findAll()).thenReturn(diagnoses);

        List<DiagnosisResponseDTO> result = diagnosisService.getAllDiagnoses();

        assertEquals(2, result.size());
        assertEquals("Diagnosis 1", result.get(0).getDescription());
        assertEquals("Diagnosis 2", result.get(1).getDescription());
    }

    @Test
    void getMostCommonDiagnoses_ShouldReturnDiagnosesWithFrequencies()
    {
        Diagnosis diagnosis1 = new Diagnosis(1L, "Diagnosis 1", null);
        Diagnosis diagnosis2 = new Diagnosis(2L, "Diagnosis 2", null);

        List<Object[]> mockResult = List.of(
                new Object[]{diagnosis1, 10L},
                new Object[]{diagnosis2, 5L}
        );

        when(diagnosisRepository.findMostCommonDiagnoses()).thenReturn(mockResult);

        List<DiagnosisFrequencyDTO> result = diagnosisService.getMostCommonDiagnoses();

        assertEquals(2, result.size());
        assertEquals("Diagnosis 1", result.get(0).getDiagnosis().getDescription());
        assertEquals(10L, result.get(0).getFrequency());
        assertEquals("Diagnosis 2", result.get(1).getDiagnosis().getDescription());
        assertEquals(5L, result.get(1).getFrequency());
    }
}