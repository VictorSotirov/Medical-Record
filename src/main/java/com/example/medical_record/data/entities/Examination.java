package com.example.medical_record.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "examinations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Examination
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate examinationDate;

    @NotBlank
    private String treatmentDescription;

    @NotNull
    private int sickLeaveDays;

    @NotNull
    private LocalDate sickLeaveStartDate;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "diagnosis_id")
    @NotNull
    private Diagnosis diagnosis;
}
