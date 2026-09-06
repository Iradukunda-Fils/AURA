package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ScoreBreakdown;

/**
 * ScoringEngine implementing deterministic multi-objective evaluation of feasible candidate resources.
 */
public class ScoringEngine {

    public ScoreBreakdown calculateScore(AcademicActivity activity, Resource resource, TimeSlot timeSlot, ScoringWeightPolicy weights) {
        if (activity == null || resource == null || timeSlot == null) {
            return new ScoreBreakdown(0, 0, 0, 0, 0, 0);
        }

        ScoringWeightPolicy policy = weights != null ? weights : ScoringWeightPolicy.defaultPolicy();

        // 1. Capacity Fit Score (C): 0 to 100. Rewards resource that satisfies capacity without excessive waste.
        int demand = activity.getStudentCount();
        int capacity = resource.getCapacity();
        double capacityRatio = (double) demand / (double) capacity;
        double capacityFitScore = Math.max(0.0, Math.min(100.0, capacityRatio * 100.0));

        // 2. Preference Alignment Score (P): 0 to 100.
        double preferenceScore = 50.0; // Default neutral
        if (activity.getPreferredTimeSlot() != null) {
            TimeSlot preferred = activity.getPreferredTimeSlot();
            if (preferred.equals(timeSlot)) {
                preferenceScore = 100.0;
            } else if (preferred.getDayOfWeek() == timeSlot.getDayOfWeek()) {
                preferenceScore = 70.0;
            } else {
                preferenceScore = 30.0;
            }
        }

        // 3. Resource Utilization Score (U): 0 to 100.
        double utilizationScore = 80.0;
        if (capacityFitScore > 85.0) {
            utilizationScore = 95.0; // Optimal utilization
        } else if (capacityFitScore < 50.0) {
            utilizationScore = 50.0; // Low utilization / large empty space
        }

        // 4. Departmental Fairness Score (F): 0 to 100.
        double fairnessScore = 85.0;

        // 5. Location Suitability Score (L): 0 to 100.
        double locationScore = 100.0;
        if (activity.getPreferredSiteId() != null && !activity.getPreferredSiteId().trim().isEmpty()) {
            if (activity.getPreferredSiteId().equalsIgnoreCase(resource.getSiteId())) {
                locationScore = 100.0;
            } else {
                locationScore = 30.0;
            }
        }

        // Calculate weighted sum
        double totalScore = (policy.getWeightCapacityFit() * capacityFitScore) +
                            (policy.getWeightPreference() * preferenceScore) +
                            (policy.getWeightUtilization() * utilizationScore) +
                            (policy.getWeightFairness() * fairnessScore) +
                            (policy.getWeightLocation() * locationScore);

        return new ScoreBreakdown(capacityFitScore, preferenceScore, utilizationScore, fairnessScore, locationScore, totalScore);
    }
}
