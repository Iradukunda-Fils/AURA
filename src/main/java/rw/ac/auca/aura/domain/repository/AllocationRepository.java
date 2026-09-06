package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.allocation.Allocation;

import java.util.List;
import java.util.Optional;

public interface AllocationRepository {
    Optional<Allocation> findById(String id);
    List<Allocation> findAll();
    List<Allocation> findByRunId(String runId);
    List<Allocation> findCommitted();
    void save(Allocation allocation);
    void saveAll(List<Allocation> allocations);
    void delete(String id);
}
