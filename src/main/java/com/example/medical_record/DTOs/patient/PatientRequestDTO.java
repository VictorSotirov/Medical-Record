package com.example.medical_record.DTOs.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRequestDTO
{
    @NotBlank(message = "First name cannot be blank!")
    @Size(max = 20, message = "First name has to be up to 20 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters and be one word!")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    @Size(max = 20, message = "Last name has to be up to 20 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only letters and be one word!")
    private String lastName;

    private boolean isHealthInsurancePaid;

    private Long personalDoctorId;
}
