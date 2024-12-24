package com.example.medical_record.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "First name cannot be blank!")
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @NotBlank(message = "Specialty name cannot be blank!")
    @Column(name = "specialty", nullable = false, length = 20)
    private String specialty;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted = false;


    @OneToMany(mappedBy = "personalDoctor")
    private Set<Patient> registeredPatients;

    @OneToMany(mappedBy = "doctor")
    private Set<Examination> examinations;

    @OneToMany(mappedBy = "doctor")
    private Set<HospitalRecord> hospitalRecords;

}
