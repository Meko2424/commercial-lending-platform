package com.mekonnen.commercial_lending_platform.dto;

import com.mekonnen.commercial_lending_platform.entity.LoanApplicationStatus;
import jakarta.validation.constraints.NotNull;

public class LoanApplicationDecisionRequest {

    @NotNull
    private LoanApplicationStatus decision;

    public LoanApplicationDecisionRequest() {
    }

    public LoanApplicationStatus getDecision() {
        return decision;
    }

    public void setDecision(LoanApplicationStatus decision) {
        this.decision = decision;
    }
}
