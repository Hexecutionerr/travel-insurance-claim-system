package com.academic.travelinsurance.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for a reviewer's reject action.
 * The rejection reason is required when rejecting a claim.
 */
public class RejectRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

    // ---- Getters and Setters ----

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
