package com.example.medical_record.services;

import com.example.medical_record.DTOs.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService
{
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    void createAdmin(UserDTO userDTO);
    void updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
