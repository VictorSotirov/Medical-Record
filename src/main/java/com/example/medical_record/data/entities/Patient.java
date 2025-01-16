package com.example.medical_record.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name cannot be blank!")
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Builder.Default
    @Column(name = "is_health_insurance_paid", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isHealthInsurancePaid = false;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;

    @OneToMany(mappedBy = "patient")
    private Set<Examination> examinations;

    @OneToMany(mappedBy = "patient")
    private Set<HospitalRecord> hospitalRecords;

}
