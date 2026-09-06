package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_policies", uniqueConstraints = {
    @UniqueConstraint(name = "uk_policy_version", columnNames = {"version"})
})
public class PolicyEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(nullable = false)
    private String description;

    @Column(name = "weight_continuity", nullable = false)
    private double weightContinuity;

    @Column(name = "weight_proximity", nullable = false)
    private double weightProximity;

    @Column(name = "weight_utilization", nullable = false)
    private double weightUtilization;

    @Column(name = "weight_preference", nullable = false)
    private double weightPreference;

    @Column(name = "weight_load_balance", nullable = false)
    private double weightLoadBalance;

    @Column(nullable = false)
    private boolean active;

    public PolicyEntity() {}

    public PolicyEntity(String id, String version, String description, double weightContinuity, double weightProximity, double weightUtilization, double weightPreference, double weightLoadBalance, boolean active) {
        this.id = id;
        this.version = version;
        this.description = description;
        this.weightContinuity = weightContinuity;
        this.weightProximity = weightProximity;
        this.weightUtilization = weightUtilization;
        this.weightPreference = weightPreference;
        this.weightLoadBalance = weightLoadBalance;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getWeightContinuity() { return weightContinuity; }
    public void setWeightContinuity(double weightContinuity) { this.weightContinuity = weightContinuity; }

    public double getWeightProximity() { return weightProximity; }
    public void setWeightProximity(double weightProximity) { this.weightProximity = weightProximity; }

    public double getWeightUtilization() { return weightUtilization; }
    public void setWeightUtilization(double weightUtilization) { this.weightUtilization = weightUtilization; }

    public double getWeightPreference() { return weightPreference; }
    public void setWeightPreference(double weightPreference) { this.weightPreference = weightPreference; }

    public double getWeightLoadBalance() { return weightLoadBalance; }
    public void setWeightLoadBalance(double weightLoadBalance) { this.weightLoadBalance = weightLoadBalance; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyEntity that = (PolicyEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
