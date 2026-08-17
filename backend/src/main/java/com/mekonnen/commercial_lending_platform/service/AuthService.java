package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.dto.LoginRequest;
import com.mekonnen.commercial_lending_platform.dto.LoginResponse;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {

        Employee employee = employeeRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password.")
                );

        if (!employee.isActive()) {
            throw new IllegalArgumentException("Employee account is inactive.");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                employee.getPasswordHash()
        )) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return new LoginResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getRole()
        );
    }
}
