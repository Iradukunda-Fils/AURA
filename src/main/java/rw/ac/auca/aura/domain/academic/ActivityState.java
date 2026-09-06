package rw.ac.auca.aura.domain.academic;

/**
 * State lifecycle transitions for an AcademicActivity.
 * DRAFT -> SUBMITTED -> EVALUATING -> PROPOSED -> ALLOCATED -> COMPLETED
 * (Or WAITLISTED, REJECTED, CANCELLED)
 */
public enum ActivityState {
    DRAFT,
    SUBMITTED,
    EVALUATING,
    PROPOSED,
    ALLOCATED,
    WAITLISTED,
    REJECTED,
    COMPLETED,
    CANCELLED
}
