package com.example.medical_record.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private boolean enabled;

    private Set<Long> roleIds = new HashSet<>();
}
