package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.dto.LoginRequest;
import com.mekonnen.commercial_lending_platform.dto.LoginResponse;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import com.mekonnen.commercial_lending_platform.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_shouldReturnEmployeeWhenCredentialsAreValid() {
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setPasswordHash("$2a$10$hashedPassword");
        employee.setRole(EmployeeRole.ANALYST);
        employee.setActive(true);

        LoginRequest request = new LoginRequest();
        request.setEmail("john.smith@example.com");
        request.setPassword("Password123!");

        when(employeeRepository.findByEmail("john.smith@example.com"))
                .thenReturn(Optional.of(employee));

        when(passwordEncoder.matches(
                "Password123!",
                "$2a$10$hashedPassword"
        )).thenReturn(true);

        LoginResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals(employeeId, response.getEmployeeId());
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("john.smith@example.com", response.getEmail());
        assertEquals(EmployeeRole.ANALYST, response.getRole());

        verify(passwordEncoder).matches(
                "Password123!",
                "$2a$10$hashedPassword"
        );
    }

    @Test
    void authenticate_shouldRejectInvalidPassword() {
        Employee employee = new Employee();
        employee.setEmail("john.smith@example.com");
        employee.setPasswordHash("$2a$10$hashedPassword");
        employee.setActive(true);

        LoginRequest request = new LoginRequest();
        request.setEmail("john.smith@example.com");
        request.setPassword("WrongPassword!");

        when(employeeRepository.findByEmail("john.smith@example.com"))
                .thenReturn(Optional.of(employee));

        when(passwordEncoder.matches(
                "WrongPassword!",
                "$2a$10$hashedPassword"
        )).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );
    }

    @Test
    void authenticate_shouldRejectUnknownEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123!");

        when(employeeRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );
    }

    @Test
    void authenticate_shouldRejectInactiveEmployee() {
        Employee employee = new Employee();
        employee.setEmail("john.smith@example.com");
        employee.setPasswordHash("$2a$10$hashedPassword");
        employee.setActive(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("john.smith@example.com");
        request.setPassword("Password123!");

        when(employeeRepository.findByEmail("john.smith@example.com"))
                .thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );

        assertEquals(
                "Employee account is inactive.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }
}
