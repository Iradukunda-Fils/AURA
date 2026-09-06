package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.ResourceEntity;

import java.util.List;
import java.util.Optional;

/**
 * Hibernate ORM Repository for Resource persistence operations.
 */
public class HibernateResourceRepository {

    public void save(ResourceEntity entity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<ResourceEntity> findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            ResourceEntity entity = em.find(ResourceEntity.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    public List<ResourceEntity> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM ResourceEntity r", ResourceEntity.class).getResultList();
        } finally {
            em.close();
        }
    }
}
