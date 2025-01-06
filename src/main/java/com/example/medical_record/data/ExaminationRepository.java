package com.example.medical_record.data;

import com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO;
import com.example.medical_record.data.entities.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long>
{
    Optional<Examination> findByIdAndIsDeletedFalse(Long id);

    List<Examination> findByIsDeletedFalse();

    //GET EXAMINATION COUNT FOR EACH DOCTOR
    @Query("SELECT new com.example.medical_record.DTOs.doctor.DoctorExaminationCountDTO(d.id, d.firstName, d.lastName, d.specialty, COUNT(e)) " +
            "FROM Examination e JOIN e.doctor d " +
            "WHERE e.isDeleted = false AND d.isDeleted = false " +
            "GROUP BY d.id, d.firstName, d.lastName, d.specialty")
    List<DoctorExaminationCountDTO> getExaminationCountsByDoctor();

    //GET ALL EXAMINATIONS GROUPED BY PATIENT
    List<Examination> findByPatientIdAndIsDeletedFalse(Long patientId);

    List<Examination> findByDoctorIdAndExaminationDateBetweenAndIsDeletedFalse(Long doctorId, LocalDate startDate, LocalDate endDate);
}
