package com.example.medical_record.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name cannot be blank!")
    @Size(max = 30, message = "First name has to be up to 20 characters!")
    @Column(name = "first_name", nullable = false, length = 20)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters!")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    @Size(max = 30, message = "Last name has to be up to 20 characters!")
    @Column(name = "last_name", nullable = false, length = 20)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only letters!")
    private String lastName;

    @Column(name = "is_health_insurance_paid", nullable = false)
    private boolean isHealthInsurancePaid;

    @ManyToOne
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<Examination> examinations;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<HospitalRecord> hospitalRecords;

}
