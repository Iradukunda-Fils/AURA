package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.policy.AllocationPolicy;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    Optional<AllocationPolicy> findById(String id);
    Optional<AllocationPolicy> findActivePolicy();
    List<AllocationPolicy> findAll();
    void save(AllocationPolicy policy);
}
