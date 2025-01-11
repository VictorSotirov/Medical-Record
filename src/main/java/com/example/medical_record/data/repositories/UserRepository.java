package com.example.medical_record.data.repositories;

import com.example.medical_record.data.entities.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndEnabledTrue(Long id);
}
