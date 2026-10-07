package com.academic.travelinsurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a travel insurance claim.
 * Maps to the CLAIM table in the H2 database.
 */
@Entity
@Table(name = "CLAIM")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @NotBlank(message = "Policy number is required")
    @Column(nullable = false)
    private String policyNumber;

    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;

    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;

    @NotNull(message = "Travel date is required")
    @Column(nullable = false)
    private LocalDate travelDate;

    @NotBlank(message = "Claim type is required")
    @Column(nullable = false)
    private String claimType;

    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than zero")
    @Column(nullable = false)
    private BigDecimal claimAmount;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotBlank(message = "Document reference is required")
    @Column(nullable = false)
    private String documentReference;

    // Status is always set by the system; never accepted from user input
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    // Only set when reviewer rejects a claim
    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Set timestamps automatically before persisting
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Update timestamp on every update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---- Constructors ----

    public Claim() {}

    // ---- Getters and Setters ----

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

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

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
