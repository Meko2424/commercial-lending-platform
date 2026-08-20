package com.mekonnen.commercial_lending_platform.dto;

import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoanApplicationDecisionRequest {

    @NotNull
    private LoanApplicationStatus decision;

    @NotBlank
    @Size(max = 1000)
    private String decisionReason;

    public LoanApplicationDecisionRequest() {
    }

    public LoanApplicationStatus getDecision() {
        return decision;
    }

    public void setDecision(LoanApplicationStatus decision) {
        this.decision = decision;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
}
