package rw.ac.auca.aura.domain.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object holding multi-objective soft constraint score contributions.
 * Score = wc * C + wp * P + wu * U + wf * F + wl * L
 */
public final class ScoreBreakdown implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double capacityFitScore;      // C (0.0 to 100.0)
    private final double preferenceScore;       // P (0.0 to 100.0)
    private final double utilizationScore;      // U (0.0 to 100.0)
    private final double fairnessScore;         // F (0.0 to 100.0)
    private final double locationScore;         // L (0.0 to 100.0)
    private final double totalScore;            // Weighted final score

    public ScoreBreakdown(double capacityFitScore, double preferenceScore, double utilizationScore,
                          double fairnessScore, double locationScore, double totalScore) {
        this.capacityFitScore = Math.max(0.0, Math.min(100.0, capacityFitScore));
        this.preferenceScore = Math.max(0.0, Math.min(100.0, preferenceScore));
        this.utilizationScore = Math.max(0.0, Math.min(100.0, utilizationScore));
        this.fairnessScore = Math.max(0.0, Math.min(100.0, fairnessScore));
        this.locationScore = Math.max(0.0, Math.min(100.0, locationScore));
        this.totalScore = totalScore;
    }

    public double getCapacityFitScore() { return capacityFitScore; }
    public double getPreferenceScore() { return preferenceScore; }
    public double getUtilizationScore() { return utilizationScore; }
    public double getFairnessScore() { return fairnessScore; }
    public double getLocationScore() { return locationScore; }
    public double getTotalScore() { return totalScore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreBreakdown that = (ScoreBreakdown) o;
        return Double.compare(that.capacityFitScore, capacityFitScore) == 0 &&
                Double.compare(that.preferenceScore, preferenceScore) == 0 &&
                Double.compare(that.utilizationScore, utilizationScore) == 0 &&
                Double.compare(that.fairnessScore, fairnessScore) == 0 &&
                Double.compare(that.locationScore, locationScore) == 0 &&
                Double.compare(that.totalScore, totalScore) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacityFitScore, preferenceScore, utilizationScore, fairnessScore, locationScore, totalScore);
    }

    @Override
    public String toString() {
        return String.format("Total: %.1f [CapacityFit: %.1f, Pref: %.1f, Util: %.1f, Fair: %.1f, Loc: %.1f]",
                totalScore, capacityFitScore, preferenceScore, utilizationScore, fairnessScore, locationScore);
    }
}
