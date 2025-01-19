package com.example.medical_record.DTOs.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class DoctorRequestDTO
{
    @NotBlank(message = "First name cannot be blank!")
    @Size(max = 20, message = "First name has to be up to 20 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters and be one word!")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    @Size(max = 20, message = "Last name has to be up to 20 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only letters and be one word!")
    private String lastName;

    @NotBlank(message = "Specialty name cannot be blank!")
    @Size(max = 20, message = "Specialty has to be up to 20 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Specialty must contain only letters!")
    private String specialty;
}
