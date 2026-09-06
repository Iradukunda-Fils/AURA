package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;
import rw.ac.auca.aura.infrastructure.persistence.entities.PolicyEntity;

public class PolicyMapper {

    public static AllocationPolicy toDomain(PolicyEntity entity) {
        if (entity == null) return null;

        ScoringWeightPolicy weights = new ScoringWeightPolicy(
                entity.getWeightContinuity(),
                entity.getWeightProximity(),
                entity.getWeightUtilization(),
                entity.getWeightPreference(),
                entity.getWeightLoadBalance()
        );

        return new AllocationPolicy(
                entity.getId(),
                entity.getVersion(),
                entity.getDescription(),
                weights,
                entity.isActive()
        );
    }

    public static PolicyEntity toEntity(AllocationPolicy domain) {
        if (domain == null) return null;

        ScoringWeightPolicy w = domain.getWeights() != null ? domain.getWeights() : ScoringWeightPolicy.defaultPolicy();

        return new PolicyEntity(
                domain.getId(),
                domain.getVersion(),
                domain.getName(),
                w.getWeightCapacityFit(),
                w.getWeightLocation(),
                w.getWeightUtilization(),
                w.getWeightPreference(),
                w.getWeightFairness(),
                domain.isActive()
        );
    }
}
