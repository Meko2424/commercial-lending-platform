package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.repository.LoanApplicationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;

    public LoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    public LoanApplication createLoanApplication(
            String businessName,
            BigDecimal requestedAmount,
            String purpose,
            Employee employee
    ) {
        LoanApplication application = new LoanApplication();

        application.setBusinessName(businessName);
        application.setRequestedAmount(requestedAmount);
        application.setPurpose(purpose);
        application.setCreatedBy(employee);

        return loanApplicationRepository.save(application);
    }

    public LoanApplication getLoanApplication(
            UUID id,
            UUID employeeId,
            boolean isAdmin
    ) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Loan application not found."
                        )
                );

        if (!isAdmin && !application.getCreatedBy().getId().equals(employeeId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this loan application."
            );
        }

        return application;
    }
}