package com.academic.travelinsurance.repository;

import com.academic.travelinsurance.entity.Claim;
import com.academic.travelinsurance.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Claim database operations.
 * Spring Data JPA automatically provides CRUD implementations.
 */
@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /**
     * Returns all claims that currently have the given status.
     * Used by the reviewer to fetch all PENDING claims.
     */
    List<Claim> findByStatus(ClaimStatus status);

    /**
     * Returns all claims sorted by creation date descending (newest first).
     * Used for the claim history view.
     */
    List<Claim> findAllByOrderByCreatedAtDesc();
}
