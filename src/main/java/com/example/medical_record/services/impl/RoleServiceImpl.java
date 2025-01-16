package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.RoleDTO;
import com.example.medical_record.data.entities.auth.Role;
import com.example.medical_record.data.repositories.RoleRepository;
import com.example.medical_record.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService
{
    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getAllRoles()
    {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(Long id)
    {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role with ID " + id + " not found."));

        return mapToDTO(role);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO)
    {
        // Optional: check if authority already exists, etc.
        Role role = new Role();

        role.setAuthority("ROLE_" + roleDTO.getAuthority().toUpperCase());

        Role savedRole = roleRepository.save(role);

        return mapToDTO(savedRole);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO)
    {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        existingRole.setAuthority("ROLE_" + roleDTO.getAuthority().toUpperCase());

        Role updatedRole = roleRepository.save(existingRole);

        return mapToDTO(updatedRole);
    }

    @Override
    public void deleteRole(Long id)
    {
        roleRepository.deleteById(id);
    }


    private RoleDTO mapToDTO(Role role)
    {
        RoleDTO dto = new RoleDTO();

        dto.setId(role.getId());

        dto.setAuthority(role.getAuthority());

        return dto;
    }
}
