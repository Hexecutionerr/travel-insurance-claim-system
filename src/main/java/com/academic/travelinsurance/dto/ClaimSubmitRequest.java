package com.academic.travelinsurance.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) for receiving claim submission data from the frontend.
 * Keeps entity separate from what the user sends over the network.
 */
public class ClaimSubmitRequest {

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Travel date is required")
    private LocalDate travelDate;

    @NotBlank(message = "Claim type is required")
    private String claimType;

    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than zero")
    private BigDecimal claimAmount;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Document reference is required")
    private String documentReference;

    // ---- Getters and Setters ----

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }

    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }

    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentReference() { return documentReference; }
    public void setDocumentReference(String documentReference) { this.documentReference = documentReference; }
}
