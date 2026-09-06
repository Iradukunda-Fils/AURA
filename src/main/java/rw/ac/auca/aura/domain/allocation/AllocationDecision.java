package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;
import rw.ac.auca.aura.domain.shared.ScoreBreakdown;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value Object holding audit-ready explainability evidence for a single allocation recommendation.
 */
public final class AllocationDecision implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String activityId;
    private final String selectedResourceId;
    private final TimeSlot selectedTimeSlot;
    private final String selectedSiteId;
    private final boolean feasible;
    private final double score;
    private final ScoreBreakdown scoreBreakdown;
    private final List<ConstraintResult> constraintResults;
    private final List<String> rejectedAlternatives;
    private final String humanExplanation;

    public AllocationDecision(String activityId, String selectedResourceId, TimeSlot selectedTimeSlot,
                              String selectedSiteId, boolean feasible, double score,
                              ScoreBreakdown scoreBreakdown, List<ConstraintResult> constraintResults,
                              List<String> rejectedAlternatives, String humanExplanation) {
        this.activityId = Objects.requireNonNull(activityId, "Activity ID cannot be null");
        this.selectedResourceId = selectedResourceId != null ? selectedResourceId : "";
        this.selectedTimeSlot = selectedTimeSlot;
        this.selectedSiteId = selectedSiteId != null ? selectedSiteId : "";
        this.feasible = feasible;
        this.score = score;
        this.scoreBreakdown = scoreBreakdown;
        this.constraintResults = constraintResults != null ? Collections.unmodifiableList(constraintResults) : Collections.emptyList();
        this.rejectedAlternatives = rejectedAlternatives != null ? Collections.unmodifiableList(rejectedAlternatives) : Collections.emptyList();
        this.humanExplanation = humanExplanation != null ? humanExplanation : "";
    }

    public String getActivityId() { return activityId; }
    public String getSelectedResourceId() { return selectedResourceId; }
    public TimeSlot getSelectedTimeSlot() { return selectedTimeSlot; }
    public String getSelectedSiteId() { return selectedSiteId; }
    public boolean isFeasible() { return feasible; }
    public double getScore() { return score; }
    public ScoreBreakdown getScoreBreakdown() { return scoreBreakdown; }
    public List<ConstraintResult> getConstraintResults() { return constraintResults; }
    public List<String> getRejectedAlternatives() { return rejectedAlternatives; }
    public String getHumanExplanation() { return humanExplanation; }

    @Override
    public String toString() {
        return String.format("Decision [Activity: %s, Resource: %s, Time: %s, Feasible: %b, Score: %.1f]",
                activityId, selectedResourceId, selectedTimeSlot, feasible, score);
    }
}
