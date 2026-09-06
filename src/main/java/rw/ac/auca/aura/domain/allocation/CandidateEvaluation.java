package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;
import rw.ac.auca.aura.domain.shared.ScoreBreakdown;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value Object holding the complete evaluation evidence for a candidate (Resource, TimeSlot) assignment.
 */
public final class CandidateEvaluation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Resource resource;
    private final TimeSlot timeSlot;
    private final boolean feasible;
    private final List<ConstraintResult> constraintResults;
    private final ScoreBreakdown scoreBreakdown;

    public CandidateEvaluation(Resource resource, TimeSlot timeSlot, boolean feasible,
                               List<ConstraintResult> constraintResults, ScoreBreakdown scoreBreakdown) {
        this.resource = Objects.requireNonNull(resource, "Resource cannot be null");
        this.timeSlot = Objects.requireNonNull(timeSlot, "TimeSlot cannot be null");
        this.feasible = feasible;
        this.constraintResults = constraintResults != null ? Collections.unmodifiableList(constraintResults) : Collections.emptyList();
        this.scoreBreakdown = scoreBreakdown;
    }

    public Resource getResource() { return resource; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public boolean isFeasible() { return feasible; }
    public List<ConstraintResult> getConstraintResults() { return constraintResults; }
    public ScoreBreakdown getScoreBreakdown() { return scoreBreakdown; }

    public double getScore() {
        return scoreBreakdown != null ? scoreBreakdown.getTotalScore() : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Candidate [%s @ %s, Feasible: %b, Score: %.1f]",
                resource.getName(), timeSlot, feasible, getScore());
    }
}
