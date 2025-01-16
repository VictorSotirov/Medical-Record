package com.example.medical_record.services.impl;

import com.example.medical_record.DTOs.UserDTO;
import com.example.medical_record.data.entities.auth.Role;
import com.example.medical_record.data.entities.auth.User;
import com.example.medical_record.data.repositories.RoleRepository;
import com.example.medical_record.data.repositories.UserRepository;
import com.example.medical_record.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found."));
    }



    @Override
    public List<UserDTO> getAllUsers()
    {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return mapToDTO(user);
    }

    @Override
    public void createAdmin(UserDTO userDTO)
    {
        User admin = new User();

        admin.setUsername(userDTO.getUsername());

        admin.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        admin.setEnabled(true);

        admin.setAccountNonExpired(true);

        admin.setAccountNonLocked(true);

        admin.setCredentialsNonExpired(true);

        admin.addAuthority(roleRepository.findByAuthority("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found.")));


        this.userRepository.save(admin);
    }

    @Override
    public void updateUser(Long id, UserDTO userDTO)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setEnabled(userDTO.isEnabled());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank())
        {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }


        /*
        if (userDTO.getRoleIds() != null)
        {
            Set<Role> roles = roleRepository.findAllById(userDTO.getRoleIds())
                    .stream().collect(Collectors.toSet());
            user.setAuthorities(roles);
        }

         */


        this.userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id)
    {
        User userToDelete = this.userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        userToDelete.setEnabled(false);

        this.userRepository.save(userToDelete);
    }

    private UserDTO mapToDTO(User user)
    {
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());

        dto.setUsername(user.getUsername());

        dto.setEnabled(user.isEnabled());

        Set<Long> roleIds = user.getAuthorities()
                .stream()
                .map(r -> ((Role) r).getId())
                .collect(Collectors.toSet());
        dto.setRoleIds(roleIds);

        return dto;
    }
}
