package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationRunEntity;

import java.util.List;

/**
 * Hibernate ORM Repository for Allocation & AllocationRun persistence.
 */
public class HibernateAllocationRepository {

    public void saveRun(AllocationRunEntity runEntity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(runEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void saveAllocation(AllocationEntity allocationEntity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(allocationEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<AllocationEntity> findAllCommittedAllocations() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AllocationEntity a", AllocationEntity.class).getResultList();
        } finally {
            em.close();
        }
    }
}
