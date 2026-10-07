package com.academic.travelinsurance.service;

import com.academic.travelinsurance.dto.ClaimSubmitRequest;
import com.academic.travelinsurance.dto.RejectRequest;
import com.academic.travelinsurance.entity.Claim;
import com.academic.travelinsurance.entity.ClaimStatus;
import com.academic.travelinsurance.exception.ClaimNotFoundException;
import com.academic.travelinsurance.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer containing all business logic for claim operations.
 * Controllers delegate to this class; this class delegates to ClaimRepository.
 */
@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    // Constructor injection (preferred over @Autowired field injection)
    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    /**
     * Accepts validated claim data from DTO, maps to entity, and persists it.
     * Status is always set to PENDING on creation.
     *
     * @param request DTO containing claim data from the customer
     * @return saved Claim entity with generated ID
     */
    public Claim submitClaim(ClaimSubmitRequest request) {
        Claim claim = new Claim();
        claim.setPolicyNumber(request.getPolicyNumber());
        claim.setCustomerName(request.getCustomerName());
        claim.setDestination(request.getDestination());
        claim.setTravelDate(request.getTravelDate());
        claim.setClaimType(request.getClaimType());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setDescription(request.getDescription());
        claim.setDocumentReference(request.getDocumentReference());
        claim.setStatus(ClaimStatus.PENDING); // Always starts as PENDING

        return claimRepository.save(claim);
    }

    /**
     * Retrieves a claim by its unique ID.
     *
     * @param claimId the claim's unique ID
     * @return the Claim entity
     * @throws ClaimNotFoundException if no claim exists with the given ID
     */
    public Claim getClaimById(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
    }

    /**
     * Returns all claims regardless of status, sorted newest first.
     * Used by the reviewer to see full claim history.
     *
     * @return list of all claims
     */
    public List<Claim> getAllClaims() {
        return claimRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Returns all claims with status PENDING.
     * Used by the reviewer to see the work queue.
     *
     * @return list of pending claims
     */
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus(ClaimStatus.PENDING);
    }

    /**
     * Approves a claim: sets status to APPROVED, clears any rejection reason.
     *
     * @param claimId the claim to approve
     * @return updated Claim entity
     */
    public Claim approveClaim(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setRejectionReason(null); // Clear if it was previously rejected
        return claimRepository.save(claim);
    }

    /**
     * Rejects a claim: sets status to REJECTED and stores the reviewer's reason.
     *
     * @param claimId the claim to reject
     * @param request DTO containing the rejection reason
     * @return updated Claim entity
     */
    public Claim rejectClaim(Long claimId, RejectRequest request) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(request.getRejectionReason());
        return claimRepository.save(claim);
    }
}
