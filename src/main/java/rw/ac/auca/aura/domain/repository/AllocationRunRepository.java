package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.allocation.AllocationRun;

import java.util.List;
import java.util.Optional;

public interface AllocationRunRepository {
    Optional<AllocationRun> findById(String runId);
    List<AllocationRun> findAll();
    void save(AllocationRun run);
    void delete(String runId);
}
