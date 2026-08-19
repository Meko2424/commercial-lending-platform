package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.dto.CreateEmployeeRequest;
import com.mekonnen.commercial_lending_platform.dto.EmployeeResponse;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request
    ) {
        Employee employee = employeeService.createEmployee(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EmployeeResponse.fromEntity(employee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(
            @PathVariable UUID id
    ) {
        Employee employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                EmployeeResponse.fromEntity(employee)
        );
    }
}
