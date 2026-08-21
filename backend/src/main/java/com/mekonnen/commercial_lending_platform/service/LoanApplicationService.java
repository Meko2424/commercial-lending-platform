package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import com.mekonnen.commercial_lending_platform.exception.LoanApplicationNotFoundException;
import com.mekonnen.commercial_lending_platform.repository.LoanApplicationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    public LoanApplication getLoanApplication(UUID id) {

        return loanApplicationRepository.findById(id)
                .orElseThrow(() ->
                        new LoanApplicationNotFoundException(
                                "Loan application not found."
                        )
                );
    }

    public LoanApplication moveToUnderReview(
            UUID applicationId
           // UUID employeeId
    ) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Loan application not found."
                        )
                );

//        if (!application.getCreatedBy().getId().equals(employeeId)) {
//            throw new AccessDeniedException(
//                    "You are not authorized to review this loan application."
//            );
//        }

        if (application.getStatus() != LoanApplicationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING applications can be moved to UNDER_REVIEW."
            );
        }

        application.setStatus(LoanApplicationStatus.UNDER_REVIEW);

        return loanApplicationRepository.save(application);
    }

    public LoanApplication makeDecision(
            UUID id,
            UUID adminId,
            LoanApplicationStatus decision,
            String decisionReason
    ) {
        LoanApplication application =
                loanApplicationRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Loan application not found."
                                )
                        );

        if (application.getStatus() != LoanApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only UNDER_REVIEW applications can be approved or rejected."
            );
        }

        if (decision != LoanApplicationStatus.APPROVED
                && decision != LoanApplicationStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Decision must be APPROVED or REJECTED."
            );
        }

        application.setStatus(decision);
        application.setReviewedBy(adminId);
        application.setReviewedAt(LocalDateTime.now());
        application.setDecisionReason(decisionReason);

        return loanApplicationRepository.save(application);
    }

    public List<LoanApplication> getLoanApplications(
            LoanApplicationStatus status
    ) {
        if (status == null) {
            return loanApplicationRepository.findAll();
        }

        return loanApplicationRepository.findAll()
                .stream()
                .filter(application ->
                        application.getStatus() == status
                )
                .toList();
    }
}