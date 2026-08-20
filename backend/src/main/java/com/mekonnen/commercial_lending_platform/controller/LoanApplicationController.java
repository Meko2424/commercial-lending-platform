package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.dto.CreateLoanApplicationRequest;
import com.mekonnen.commercial_lending_platform.dto.LoanApplicationDecisionRequest;
import com.mekonnen.commercial_lending_platform.dto.LoanApplicationResponse;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.service.EmployeeService;
import com.mekonnen.commercial_lending_platform.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;
    private final EmployeeService employeeService;

    public LoanApplicationController(
            LoanApplicationService loanApplicationService,
            EmployeeService employeeService
    ) {
        this.loanApplicationService = loanApplicationService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<LoanApplicationResponse> createLoanApplication(
            @Valid @RequestBody CreateLoanApplicationRequest request,
            Authentication authentication
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        Employee employee = employeeService.getEmployeeById(employeeId);

        LoanApplication application =
                loanApplicationService.createLoanApplication(
                        request.getBusinessName(),
                        request.getRequestedAmount(),
                        request.getPurpose(),
                        employee
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(LoanApplicationResponse.fromEntity(application));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponse> getLoanApplication(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        LoanApplication application =
                loanApplicationService.getLoanApplication(
                        id,
                        employeeId,
                        isAdmin
                );

        return ResponseEntity.ok(
                LoanApplicationResponse.fromEntity(application)
        );
    }

    @PreAuthorize("hasRole('ANALYST')")
    @PatchMapping("/{id}/under-review")
    public ResponseEntity<LoanApplicationResponse> moveToUnderReview(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        LoanApplication application =
                loanApplicationService.moveToUnderReview(
                        id,
                        employeeId
                );

        return ResponseEntity.ok(
                LoanApplicationResponse.fromEntity(application)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/decision")
    public ResponseEntity<LoanApplicationResponse> makeDecision(
            @PathVariable UUID id,
            @Valid @RequestBody LoanApplicationDecisionRequest request,
            Authentication authentication
    ) {
        UUID adminId = UUID.fromString(authentication.getName());

        LoanApplication application =
                loanApplicationService.makeDecision(
                        id,
                        adminId,
                        request.getDecision()
                );

        return ResponseEntity.ok(
                LoanApplicationResponse.fromEntity(application)
        );
    }
}
