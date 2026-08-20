package com.mekonnen.commercial_lending_platform.dto;

import com.mekonnen.commercial_lending_platform.entity.LoanApplication;
import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanApplicationResponse {

    private UUID id;
    private String businessName;
    private BigDecimal requestedAmount;
    private String purpose;
    private LoanApplicationStatus status;
    private UUID createdBy;

    public LoanApplicationResponse() {
    }

    public LoanApplicationResponse(
            UUID id,
            String businessName,
            BigDecimal requestedAmount,
            String purpose,
            LoanApplicationStatus status,
            UUID createdBy
    ) {
        this.id = id;
        this.businessName = businessName;
        this.requestedAmount = requestedAmount;
        this.purpose = purpose;
        this.status = status;
        this.createdBy = createdBy;
    }

    public static LoanApplicationResponse fromEntity(
            LoanApplication application
    ) {
        return new LoanApplicationResponse(
                application.getId(),
                application.getBusinessName(),
                application.getRequestedAmount(),
                application.getPurpose(),
                application.getStatus(),
                application.getCreatedBy().getId()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }
}