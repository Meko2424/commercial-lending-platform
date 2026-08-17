package com.mekonnen.commercial_lending_platform.dto;

import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;

import java.util.UUID;

public class LoginResponse {

    private UUID employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeRole role;

    public LoginResponse(
            UUID employeeId,
            String firstName,
            String lastName,
            String email,
            EmployeeRole role
    ) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeRole getRole() {
        return role;
    }
}
