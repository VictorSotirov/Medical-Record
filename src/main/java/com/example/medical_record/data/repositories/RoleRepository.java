package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Role> findByAuthority(String authority);
}
