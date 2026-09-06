package rw.ac.auca.aura.domain.policy;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain Aggregate representing institutional allocation policy versioning.
 */
public class AllocationPolicy implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String version;
    private final String name;
    private final ScoringWeightPolicy weights;
    private final boolean active;

    public AllocationPolicy(String id, String version, String name, ScoringWeightPolicy weights, boolean active) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy ID cannot be empty");
        }
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy version cannot be empty");
        }
        this.id = id;
        this.version = version;
        this.name = name != null ? name : "Institutional Allocation Policy " + version;
        this.weights = weights != null ? weights : ScoringWeightPolicy.defaultPolicy();
        this.active = active;
    }

    public static AllocationPolicy defaultBaseline() {
        return new AllocationPolicy("POL-DEFAULT", "v1.0", "AUCA Standard Baseline Policy 2026",
                ScoringWeightPolicy.defaultPolicy(), true);
    }

    public String getId() { return id; }
    public String getVersion() { return version; }
    public String getName() { return name; }
    public ScoringWeightPolicy getWeights() { return weights; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationPolicy policy = (AllocationPolicy) o;
        return Objects.equals(id, policy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + version + ")";
    }
}
