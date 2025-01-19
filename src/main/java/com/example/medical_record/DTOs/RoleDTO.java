package com.example.medical_record.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO
{
    private Long id;

    @NotBlank(message = "Role cannot be blank!")
    private String authority;
}
