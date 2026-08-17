package com.mekonnen.commercial_lending_platform.dto;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;

import java.util.UUID;

public class EmployeeResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeRole role;
    private boolean active;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            UUID id,
            String firstName,
            String lastName,
            String email,
            EmployeeRole role,
            boolean active
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getRole(),
                employee.isActive()
        );
    }

    public UUID getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }
}
