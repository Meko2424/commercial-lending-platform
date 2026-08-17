package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import com.mekonnen.commercial_lending_platform.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_shouldCreateEmployeeWithEncodedPassword() {
        String rawPassword = "Password123!";
        String encodedPassword = "$2a$10$encodedPassword";

        when(employeeRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Employee employee = employeeService.createEmployee(
                "John",
                "Smith",
                "john@example.com",
                rawPassword,
                EmployeeRole.ANALYST
        );

        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("john@example.com", employee.getEmail());
        assertEquals(encodedPassword, employee.getPasswordHash());
        assertEquals(EmployeeRole.ANALYST, employee.getRole());
        assertTrue(employee.isActive());

        verify(passwordEncoder).encode(rawPassword);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldRejectDuplicateEmail() {
        when(employeeRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(
                        "John",
                        "Smith",
                        "john@example.com",
                        "Password123!",
                        EmployeeRole.ANALYST
                )
        );

        assertEquals("Employee email already exists.", exception.getMessage());

        verify(employeeRepository, never()).save(any(Employee.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void getEmployeeById_shouldReturnEmployeeWhenFound() {
        UUID id = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john@example.com");
        employee.setRole(EmployeeRole.UNDERWRITER);

        when(employeeRepository.findById(id))
                .thenReturn(java.util.Optional.of(employee));

        Employee result = employeeService.getEmployeeById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getEmployeeById_shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(employeeRepository.findById(id))
                .thenReturn(java.util.Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.getEmployeeById(id)
        );

        assertEquals("Employee not found.", exception.getMessage());
    }
}
