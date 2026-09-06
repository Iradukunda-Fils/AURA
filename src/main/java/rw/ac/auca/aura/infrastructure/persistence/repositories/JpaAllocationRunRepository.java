package rw.ac.auca.aura.infrastructure.persistence.repositories;

import rw.ac.auca.aura.domain.allocation.AllocationRun;
import rw.ac.auca.aura.domain.repository.AllocationRunRepository;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationRunEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.AllocationRunMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaAllocationRunRepository extends GenericDao<AllocationRunEntity, String> implements AllocationRunRepository {

    public JpaAllocationRunRepository() {
        super(AllocationRunEntity.class);
    }

    @Override
    public Optional<AllocationRun> findById(String runId) {
        return findEntityById(runId).map(AllocationRunMapper::toDomain);
    }

    @Override
    public List<AllocationRun> findAll() {
        return findAllEntities().stream()
                .map(AllocationRunMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AllocationRun run) {
        AllocationRunEntity entity = AllocationRunMapper.toEntity(run);
        saveEntity(entity);
    }

    @Override
    public void delete(String runId) {
        deleteEntity(runId);
    }
}
