package com.academic.travelinsurance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a claim is not found by ID.
 * Spring will automatically return HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClaimNotFoundException extends RuntimeException {

    public ClaimNotFoundException(Long claimId) {
        super("Claim not found with ID: " + claimId);
    }
}
