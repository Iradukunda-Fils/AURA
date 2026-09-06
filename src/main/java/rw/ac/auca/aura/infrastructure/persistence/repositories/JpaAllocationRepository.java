package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.repository.AllocationRepository;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.AllocationMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaAllocationRepository extends GenericDao<AllocationEntity, String> implements AllocationRepository {

    public JpaAllocationRepository() {
        super(AllocationEntity.class);
    }

    @Override
    public Optional<Allocation> findById(String id) {
        return findEntityById(id).map(AllocationMapper::toDomain);
    }

    @Override
    public List<Allocation> findAll() {
        return findAllEntities().stream()
                .map(AllocationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Allocation> findByRunId(String runId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<AllocationEntity> list = em.createQuery(
                    "SELECT a FROM AllocationEntity a WHERE a.runId = :runId", AllocationEntity.class)
                    .setParameter("runId", runId)
                    .getResultList();
            return list.stream().map(AllocationMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<Allocation> findCommitted() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<AllocationEntity> list = em.createQuery(
                    "SELECT a FROM AllocationEntity a WHERE a.status = :status", AllocationEntity.class)
                    .setParameter("status", Allocation.AllocationStatus.COMMITTED)
                    .getResultList();
            return list.stream().map(AllocationMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Allocation allocation) {
        AllocationEntity entity = AllocationMapper.toEntity(allocation);
        saveEntity(entity);
    }

    @Override
    public void saveAll(List<Allocation> allocations) {
        if (allocations == null || allocations.isEmpty()) return;
        for (Allocation alloc : allocations) {
            save(alloc);
        }
    }

    @Override
    public void delete(String id) {
        deleteEntity(id);
    }
}
