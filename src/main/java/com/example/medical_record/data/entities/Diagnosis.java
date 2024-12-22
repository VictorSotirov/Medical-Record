package com.example.medical_record.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
public class Diagnosis
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Submit to removal
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String description;

    @OneToMany(mappedBy = "diagnosis", cascade = CascadeType.ALL)
    private Set<Examination> examinations;
}
