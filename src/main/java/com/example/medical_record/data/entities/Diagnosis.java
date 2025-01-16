package com.example.medical_record.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Diagnosis
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String description;

    @OneToMany(mappedBy = "diagnosis")
    private Set<Examination> examinations;
}
