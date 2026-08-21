package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import com.mekonnen.commercial_lending_platform.exception.GlobalExceptionHandler;
import com.mekonnen.commercial_lending_platform.exception.LoanApplicationNotFoundException;
import com.mekonnen.commercial_lending_platform.service.EmployeeService;
import com.mekonnen.commercial_lending_platform.service.LoanApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                applicationId
                //employeeId
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
                .moveToUnderReview(applicationId);
    }

    @Test
    void moveToUnderReview_shouldReturn409WhenApplicationIsAlreadyUnderReview()
            throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(loanApplicationService.moveToUnderReview(
                applicationId
                //employeeId
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
                .moveToUnderReview(applicationId);
    }

    @Test
    void makeDecision_shouldReturn200WhenAdminApproves() throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());

        LoanApplication application = new LoanApplication();
        application.setId(applicationId);
        application.setBusinessName("Beta Logistics");
        application.setRequestedAmount(
                new BigDecimal("150000.00")
        );
        application.setPurpose("Working capital for expansion");
        application.setStatus(LoanApplicationStatus.APPROVED);
        application.setCreatedBy(employee);

        when(loanApplicationService.makeDecision(
                applicationId,
                adminId,
                LoanApplicationStatus.APPROVED,
                "Strong financial performance."
        )).thenReturn(application);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/decision",
                                applicationId
                        )
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "decision": "APPROVED",
                                        "decisionReason": "Strong financial performance."
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(applicationId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("APPROVED"));

        verify(loanApplicationService)
                .makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.APPROVED,
                        "Strong financial performance."
                );
    }

    @Test
    void makeDecision_shouldReturn200WhenAdminRejects() throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());

        LoanApplication application = new LoanApplication();
        application.setId(applicationId);
        application.setBusinessName("Beta Logistics");
        application.setRequestedAmount(
                new BigDecimal("150000.00")
        );
        application.setPurpose("Working capital for expansion");
        application.setStatus(LoanApplicationStatus.REJECTED);
        application.setCreatedBy(employee);

        when(loanApplicationService.makeDecision(
                applicationId,
                adminId,
                LoanApplicationStatus.REJECTED,
                "Insufficient cash flow."
        )).thenReturn(application);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/decision",
                                applicationId
                        )
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "decision": "REJECTED",
                                        "decisionReason": "Insufficient cash flow."
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(applicationId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("REJECTED"));

        verify(loanApplicationService)
                .makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.REJECTED,
                        "Insufficient cash flow."
                );
    }

    @Test
    void makeDecision_shouldReturn409WhenApplicationIsNotUnderReview()
            throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        when(loanApplicationService.makeDecision(
                applicationId,
                adminId,
                LoanApplicationStatus.APPROVED,
                "Strong financial performance."
        )).thenThrow(
                new IllegalStateException(
                        "Only UNDER_REVIEW applications can be approved or rejected."
                )
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/decision",
                                applicationId
                        )
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "decision": "APPROVED",
                                        "decisionReason": "Strong financial performance."
                                    }
                                    """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Only UNDER_REVIEW applications can be approved or rejected."
                        ));

        verify(loanApplicationService)
                .makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.APPROVED,
                        "Strong financial performance."
                );
    }

    @Test
    void makeDecision_shouldReturn400WhenDecisionIsMissing()
            throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/loan-applications/{id}/decision",
                                applicationId
                        )
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loanApplicationService);
    }

    @Test
    void getLoanApplications_shouldReturnAllApplications() throws Exception {

        UUID firstApplicationId = UUID.randomUUID();
        UUID secondApplicationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(employeeId);

        LoanApplication firstApplication = new LoanApplication();
        firstApplication.setId(firstApplicationId);
        firstApplication.setBusinessName("Acme Manufacturing");
        firstApplication.setRequestedAmount(
                new BigDecimal("250000.00")
        );
        firstApplication.setPurpose("Purchase equipment");
        firstApplication.setStatus(LoanApplicationStatus.PENDING);
        firstApplication.setCreatedBy(employee);

        LoanApplication secondApplication = new LoanApplication();
        secondApplication.setId(secondApplicationId);
        secondApplication.setBusinessName("Beta Logistics");
        secondApplication.setRequestedAmount(
                new BigDecimal("150000.00")
        );
        secondApplication.setPurpose("Working capital");
        secondApplication.setStatus(LoanApplicationStatus.APPROVED);
        secondApplication.setCreatedBy(employee);

        when(loanApplicationService.getLoanApplications(null))
                .thenReturn(List.of(firstApplication, secondApplication));

        mockMvc.perform(
                        get("/api/loan-applications")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id")
                        .value(firstApplicationId.toString()))
                .andExpect(jsonPath("$[0].businessName")
                        .value("Acme Manufacturing"))
                .andExpect(jsonPath("$[0].status")
                        .value("PENDING"))
                .andExpect(jsonPath("$[1].id")
                        .value(secondApplicationId.toString()))
                .andExpect(jsonPath("$[1].businessName")
                        .value("Beta Logistics"))
                .andExpect(jsonPath("$[1].status")
                        .value("APPROVED"));

        verify(loanApplicationService)
                .getLoanApplications(null);
    }

    @Test
    void getLoanApplications_shouldReturnApplicationsByStatus()
            throws Exception {

        UUID applicationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(employeeId);

        LoanApplication application = new LoanApplication();
        application.setId(applicationId);
        application.setBusinessName("Acme Manufacturing");
        application.setRequestedAmount(
                new BigDecimal("250000.00")
        );
        application.setPurpose("Purchase equipment");
        application.setStatus(LoanApplicationStatus.PENDING);
        application.setCreatedBy(employee);

        when(loanApplicationService.getLoanApplications(
                LoanApplicationStatus.PENDING
        )).thenReturn(List.of(application));

        mockMvc.perform(
                        get("/api/loan-applications")
                                .param(
                                        "status",
                                        "PENDING"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(applicationId.toString()))
                .andExpect(jsonPath("$[0].businessName")
                        .value("Acme Manufacturing"))
                .andExpect(jsonPath("$[0].status")
                        .value("PENDING"));

        verify(loanApplicationService)
                .getLoanApplications(
                        LoanApplicationStatus.PENDING
                );
    }

    @Test
    void getLoanApplication_shouldReturn404WhenApplicationDoesNotExist()
            throws Exception {

        UUID applicationId = UUID.randomUUID();

        when(loanApplicationService.getLoanApplication(applicationId))
                .thenThrow(
                        new LoanApplicationNotFoundException(
                                "Loan application not found."
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/loan-applications/{id}",
                                applicationId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Loan application not found."));

        verify(loanApplicationService)
                .getLoanApplication(applicationId);
    }
}