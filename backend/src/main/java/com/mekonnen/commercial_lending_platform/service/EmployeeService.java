package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import com.mekonnen.commercial_lending_platform.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee createEmployee(
            String firstName,
            String lastName,
            String email,
            String password,
            EmployeeRole role
    ) {
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Employee email already exists.");
        }

        Employee employee = new Employee();

        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPasswordHash(passwordEncoder.encode(password));
        employee.setRole(role);
        employee.setActive(true);

        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found.")
                );
    }
}
