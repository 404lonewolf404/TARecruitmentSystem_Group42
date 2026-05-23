package com.bupt.tarecruitment.model;

/**
 * Status values for an application record.
 */
public enum ApplicationStatus {
    /**
     * Waiting for review.
     */
    PENDING,
    
    /**
     * Accepted by the recruiter.
     */
    SELECTED,
    
    /**
     * Rejected by the recruiter.
     */
    REJECTED,
    
    /**
     * Withdrawn by the applicant.
     */
    WITHDRAWN
}
