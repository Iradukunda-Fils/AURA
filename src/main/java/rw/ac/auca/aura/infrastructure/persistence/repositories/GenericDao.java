package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;

import java.util.List;
import java.util.Optional;

public abstract class GenericDao<T, ID> {

    private final Class<T> entityClass;

    protected GenericDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> findEntityById(ID id) {
        return findEntityById(id, null);
    }

    public Optional<T> findEntityById(ID id, EntityManager em) {
        boolean localEm = (em == null);
        EntityManager currentEm = localEm ? JpaUtil.getEntityManager() : em;
        try {
            T entity = currentEm.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            if (localEm && currentEm != null && currentEm.isOpen()) {
                currentEm.close();
            }
        }
    }

    public List<T> findAllEntities() {
        return findAllEntities(null);
    }

    public List<T> findAllEntities(EntityManager em) {
        boolean localEm = (em == null);
        EntityManager currentEm = localEm ? JpaUtil.getEntityManager() : em;
        try {
            String queryStr = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return currentEm.createQuery(queryStr, entityClass).getResultList();
        } finally {
            if (localEm && currentEm != null && currentEm.isOpen()) {
                currentEm.close();
            }
        }
    }

    public void saveEntity(T entity) {
        saveEntity(entity, null);
    }

    public void saveEntity(T entity, EntityManager em) {
        boolean localEm = (em == null);
        EntityManager currentEm = localEm ? JpaUtil.getEntityManager() : em;
        EntityTransaction tx = localEm ? currentEm.getTransaction() : null;
        try {
            if (localEm) tx.begin();
            currentEm.merge(entity);
            if (localEm) tx.commit();
        } catch (Exception e) {
            if (localEm && tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (localEm && currentEm != null && currentEm.isOpen()) {
                currentEm.close();
            }
        }
    }

    public void deleteEntity(ID id) {
        deleteEntity(id, null);
    }

    public void deleteEntity(ID id, EntityManager em) {
        boolean localEm = (em == null);
        EntityManager currentEm = localEm ? JpaUtil.getEntityManager() : em;
        EntityTransaction tx = localEm ? currentEm.getTransaction() : null;
        try {
            if (localEm) tx.begin();
            T entity = currentEm.find(entityClass, id);
            if (entity != null) {
                currentEm.remove(entity);
            }
            if (localEm) tx.commit();
        } catch (Exception e) {
            if (localEm && tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (localEm && currentEm != null && currentEm.isOpen()) {
                currentEm.close();
            }
        }
    }
}
