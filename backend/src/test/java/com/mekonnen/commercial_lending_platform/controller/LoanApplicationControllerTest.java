package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import com.mekonnen.commercial_lending_platform.exception.GlobalExceptionHandler;
import com.mekonnen.commercial_lending_platform.service.EmployeeService;
import com.mekonnen.commercial_lending_platform.service.LoanApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanApplicationService loanApplicationService;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        LoanApplicationController controller =
                new LoanApplicationController(
                        loanApplicationService,
                        employeeService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void moveToUnderReview_shouldReturn200WhenSuccessful() throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(employeeId);

        LoanApplication application = new LoanApplication();
        application.setId(applicationId);
        application.setBusinessName("Beta Logistics");
        application.setRequestedAmount(
                new BigDecimal("150000.00")
        );
        application.setPurpose("Working capital for expansion");
        application.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        application.setCreatedBy(employee);

        when(loanApplicationService.moveToUnderReview(
                applicationId,
                employeeId
        )).thenReturn(application);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        employeeId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ANALYST")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/under-review",
                                applicationId
                        )
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(applicationId.toString()))
                .andExpect(jsonPath("$.businessName")
                        .value("Beta Logistics"))
                .andExpect(jsonPath("$.status")
                        .value("UNDER_REVIEW"));

        verify(loanApplicationService)
                .moveToUnderReview(applicationId, employeeId);
    }

    @Test
    void moveToUnderReview_shouldReturn409WhenApplicationIsAlreadyUnderReview()
            throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(loanApplicationService.moveToUnderReview(
                applicationId,
                employeeId
        )).thenThrow(
                new IllegalStateException(
                        "Only PENDING applications can be moved to UNDER_REVIEW."
                )
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        employeeId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ANALYST")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/under-review",
                                applicationId
                        )
                                .principal(authentication)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Only PENDING applications can be moved to UNDER_REVIEW."
                        ));

        verify(loanApplicationService)
                .moveToUnderReview(applicationId, employeeId);
    }
}