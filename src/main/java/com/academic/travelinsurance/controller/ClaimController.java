package com.academic.travelinsurance.controller;

import com.academic.travelinsurance.dto.ClaimSubmitRequest;
import com.academic.travelinsurance.entity.Claim;
import com.academic.travelinsurance.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling customer-facing claim operations.
 * Base path: /api/claims
 */
@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * POST /api/claims
     * Customer submits a new travel insurance claim.
     * @Valid triggers Bean Validation on the request body.
     * Returns 201 Created with the saved claim (including generated ID).
     */
    @PostMapping
    public ResponseEntity<Claim> submitClaim(@Valid @RequestBody ClaimSubmitRequest request) {
        Claim savedClaim = claimService.submitClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClaim);
    }

    /**
     * GET /api/claims/{claimId}
     * Customer retrieves their submitted claim by ID to view details or check status.
     * Returns 200 OK with claim data, or 404 if not found.
     */
    @GetMapping("/{claimId}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long claimId) {
        Claim claim = claimService.getClaimById(claimId);
        return ResponseEntity.ok(claim);
    }
}
