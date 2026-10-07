package com.academic.travelinsurance.controller;

import com.academic.travelinsurance.dto.RejectRequest;
import com.academic.travelinsurance.entity.Claim;
import com.academic.travelinsurance.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller handling reviewer operations.
 * Base path: /api/reviewer
 */
@RestController
@RequestMapping("/api/reviewer")
public class ReviewerController {

    private final ClaimService claimService;

    public ReviewerController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * GET /api/reviewer/claims/all
     * Reviewer retrieves all claims regardless of status (for history view).
     * Returns 200 OK with complete claim list ordered by creation date descending.
     */
    @GetMapping("/claims/all")
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> allClaims = claimService.getAllClaims();
        return ResponseEntity.ok(allClaims);
    }

    /**
     * GET /api/reviewer/claims/pending
     * Reviewer retrieves all claims currently in PENDING status.
     * Returns 200 OK with list of pending claims.
     */
    @GetMapping("/claims/pending")
    public ResponseEntity<List<Claim>> getPendingClaims() {
        List<Claim> pendingClaims = claimService.getPendingClaims();
        return ResponseEntity.ok(pendingClaims);
    }

    /**
     * GET /api/reviewer/claims/{claimId}
     * Reviewer views the full details of a specific claim before making a decision.
     * Returns 200 OK with claim data, or 404 if not found.
     */
    @GetMapping("/claims/{claimId}")
    public ResponseEntity<Claim> getClaimForReview(@PathVariable Long claimId) {
        Claim claim = claimService.getClaimById(claimId);
        return ResponseEntity.ok(claim);
    }

    /**
     * PUT /api/reviewer/claims/{claimId}/approve
     * Reviewer approves a claim. No request body needed.
     * Returns 200 OK with updated claim (status: APPROVED).
     */
    @PutMapping("/claims/{claimId}/approve")
    public ResponseEntity<Claim> approveClaim(@PathVariable Long claimId) {
        Claim updatedClaim = claimService.approveClaim(claimId);
        return ResponseEntity.ok(updatedClaim);
    }

    /**
     * PUT /api/reviewer/claims/{claimId}/reject
     * Reviewer rejects a claim with a mandatory rejection reason.
     * Returns 200 OK with updated claim (status: REJECTED, reason stored).
     */
    @PutMapping("/claims/{claimId}/reject")
    public ResponseEntity<Claim> rejectClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody RejectRequest request) {
        Claim updatedClaim = claimService.rejectClaim(claimId, request);
        return ResponseEntity.ok(updatedClaim);
    }
}
