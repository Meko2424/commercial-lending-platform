package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import com.mekonnen.commercial_lending_platform.exception.LoanApplicationNotFoundException;
import com.mekonnen.commercial_lending_platform.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    private UUID employeeId;
    private UUID otherEmployeeId;
    private UUID applicationId;
    private UUID adminId;

    private Employee employee;
    private Employee otherEmployee;
    private LoanApplication application;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        otherEmployeeId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        applicationId = UUID.randomUUID();

        employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setRole(EmployeeRole.ANALYST);
        employee.setActive(true);

        otherEmployee = new Employee();
        otherEmployee.setId(otherEmployeeId);
        otherEmployee.setFirstName("Jane");
        otherEmployee.setLastName("Doe");
        otherEmployee.setEmail("jane.doe@example.com");
        otherEmployee.setRole(EmployeeRole.ANALYST);
        otherEmployee.setActive(true);

        application = new LoanApplication();
        application.setId(applicationId);
        application.setBusinessName("Test Business");
        application.setRequestedAmount(new BigDecimal("100000.00"));
        application.setPurpose("Working capital");
        application.setCreatedBy(employee);
    }

    @Test
    void getLoanApplication_shouldReturnApplicationWhenItExists() {

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        LoanApplication result =
                loanApplicationService.getLoanApplication(applicationId);

        assertNotNull(result);
        assertEquals(applicationId, result.getId());
        assertEquals(
                employeeId,
                result.getCreatedBy().getId()
        );

        verify(loanApplicationRepository)
                .findById(applicationId);
    }

    @Test
    void getLoanApplication_shouldThrowExceptionWhenApplicationDoesNotExist() {

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.empty());

        assertThrows(
                LoanApplicationNotFoundException.class,
                () -> loanApplicationService
                        .getLoanApplication(applicationId)
        );

        verify(loanApplicationRepository)
                .findById(applicationId);
    }

    @Test
    void moveToUnderReview_shouldChangeStatusForOwner() {

        application.setStatus(LoanApplicationStatus.PENDING);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        when(loanApplicationRepository.save(application))
                .thenReturn(application);

        LoanApplication result =
                loanApplicationService.moveToUnderReview(
                        applicationId
                        //employeeId
                );

        assertNotNull(result);
        assertEquals(
                LoanApplicationStatus.UNDER_REVIEW,
                result.getStatus()
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository).save(application);
    }

    @Test
    void moveToUnderReview_shouldRejectNonPendingApplication() {

        application.setStatus(LoanApplicationStatus.UNDER_REVIEW);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.moveToUnderReview(
                        applicationId
                        //employeeId
                )
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void makeDecision_shouldApproveUnderReviewApplication() {

        application.setStatus(LoanApplicationStatus.UNDER_REVIEW);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        when(loanApplicationRepository.save(application))
                .thenReturn(application);

        LoanApplication result =
                loanApplicationService.makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.APPROVED,
                        "Strong financial performance."
                );

        assertNotNull(result);
        assertEquals(
                LoanApplicationStatus.APPROVED,
                result.getStatus()
        );
        assertEquals(adminId, result.getReviewedBy());
        assertNotNull(result.getReviewedAt());
        assertEquals(
                "Strong financial performance.",
                result.getDecisionReason()
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository).save(application);
    }

    @Test
    void makeDecision_shouldRejectUnderReviewApplication() {

        application.setStatus(LoanApplicationStatus.UNDER_REVIEW);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        when(loanApplicationRepository.save(application))
                .thenReturn(application);

        LoanApplication result =
                loanApplicationService.makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.REJECTED,
                        "Insufficient cash flow."
                );

        assertNotNull(result);
        assertEquals(
                LoanApplicationStatus.REJECTED,
                result.getStatus()
        );
        assertEquals(adminId, result.getReviewedBy());
        assertNotNull(result.getReviewedAt());
        assertEquals(
                "Insufficient cash flow.",
                result.getDecisionReason()
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository).save(application);
    }

    @Test
    void makeDecision_shouldRejectApprovalWhenApplicationIsPending() {

        application.setStatus(LoanApplicationStatus.PENDING);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.APPROVED,
                        "Strong financial performance."
                )
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void makeDecision_shouldRejectRejectionWhenApplicationIsPending() {

        application.setStatus(LoanApplicationStatus.PENDING);

        when(loanApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.makeDecision(
                        applicationId,
                        adminId,
                        LoanApplicationStatus.REJECTED,
                        "Insufficient cash flow."
                )
        );

        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void getLoanApplications_shouldReturnAllApplicationsWhenStatusIsNull() {

        LoanApplication secondApplication = new LoanApplication();
        secondApplication.setId(UUID.randomUUID());
        secondApplication.setBusinessName("Beta Logistics");
        secondApplication.setStatus(LoanApplicationStatus.APPROVED);
        secondApplication.setCreatedBy(employee);

        application.setStatus(LoanApplicationStatus.PENDING);

        when(loanApplicationRepository.findAll())
                .thenReturn(List.of(application, secondApplication));

        List<LoanApplication> result =
                loanApplicationService.getLoanApplications(null);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(loanApplicationRepository).findAll();
    }

    @Test
    void getLoanApplications_shouldReturnApplicationsByStatus() {

        LoanApplication secondApplication = new LoanApplication();
        secondApplication.setId(UUID.randomUUID());
        secondApplication.setBusinessName("Beta Logistics");
        secondApplication.setStatus(LoanApplicationStatus.APPROVED);
        secondApplication.setCreatedBy(employee);

        application.setStatus(LoanApplicationStatus.PENDING);

        when(loanApplicationRepository.findAll())
                .thenReturn(List.of(application, secondApplication));

        List<LoanApplication> result =
                loanApplicationService.getLoanApplications(
                        LoanApplicationStatus.PENDING
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                LoanApplicationStatus.PENDING,
                result.get(0).getStatus()
        );
        assertEquals(
                application.getId(),
                result.get(0).getId()
        );

        verify(loanApplicationRepository).findAll();
    }
}
