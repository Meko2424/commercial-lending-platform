package com.mekonnen.commercial_lending_platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateLoanApplicationRequest {

    @NotBlank
    @Size(max = 255)
    private String businessName;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal requestedAmount;

    @NotBlank
    @Size(max = 500)
    private String purpose;

    public CreateLoanApplicationRequest() {
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
