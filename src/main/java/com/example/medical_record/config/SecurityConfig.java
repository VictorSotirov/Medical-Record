package com.example.medical_record.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig
{
    //TODO IMPLEMENT SECURITY AFTER FINISHING ALL FUNCTIONALITIES
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for simplicity during development
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // Allow all requests without authentication

        return http.build();
    }
}
