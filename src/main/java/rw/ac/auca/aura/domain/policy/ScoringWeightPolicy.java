package rw.ac.auca.aura.domain.policy;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object / Policy Data defining multi-objective scoring weights.
 * Total utility U = wc * CapacityFit + wp * Preference + wu * Utilization + wf * Fairness + wl * Location
 */
public final class ScoringWeightPolicy implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double weightCapacityFit;    // wc
    private final double weightPreference;     // wp
    private final double weightUtilization;    // wu
    private final double weightFairness;       // wf
    private final double weightLocation;       // wl

    public ScoringWeightPolicy(double weightCapacityFit, double weightPreference,
                               double weightUtilization, double weightFairness, double weightLocation) {
        double sum = weightCapacityFit + weightPreference + weightUtilization + weightFairness + weightLocation;
        if (sum <= 0) {
            throw new IllegalArgumentException("Sum of scoring weights must be positive");
        }
        // Normalize weights to sum to 1.0
        this.weightCapacityFit = weightCapacityFit / sum;
        this.weightPreference = weightPreference / sum;
        this.weightUtilization = weightUtilization / sum;
        this.weightFairness = weightFairness / sum;
        this.weightLocation = weightLocation / sum;
    }

    public static ScoringWeightPolicy defaultPolicy() {
        return new ScoringWeightPolicy(0.35, 0.25, 0.20, 0.10, 0.10);
    }

    public double getWeightCapacityFit() { return weightCapacityFit; }
    public double getWeightPreference() { return weightPreference; }
    public double getWeightUtilization() { return weightUtilization; }
    public double getWeightFairness() { return weightFairness; }
    public double getWeightLocation() { return weightLocation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoringWeightPolicy that = (ScoringWeightPolicy) o;
        return Double.compare(that.weightCapacityFit, weightCapacityFit) == 0 &&
                Double.compare(that.weightPreference, weightPreference) == 0 &&
                Double.compare(that.weightUtilization, weightUtilization) == 0 &&
                Double.compare(that.weightFairness, weightFairness) == 0 &&
                Double.compare(that.weightLocation, weightLocation) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weightCapacityFit, weightPreference, weightUtilization, weightFairness, weightLocation);
    }

    @Override
    public String toString() {
        return String.format("Weights [CapacityFit: %.2f, Pref: %.2f, Util: %.2f, Fair: %.2f, Loc: %.2f]",
                weightCapacityFit, weightPreference, weightUtilization, weightFairness, weightLocation);
    }
}
