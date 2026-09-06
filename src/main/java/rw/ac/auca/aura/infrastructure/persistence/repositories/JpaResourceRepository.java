package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.repository.ResourceRepository;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.BuildingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ResourceEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.ResourceMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaResourceRepository extends GenericDao<ResourceEntity, String> implements ResourceRepository {

    public JpaResourceRepository() {
        super(ResourceEntity.class);
    }

    @Override
    public Optional<Resource> findById(String id) {
        return findEntityById(id).map(ResourceMapper::toDomain);
    }

    @Override
    public List<Resource> findAll() {
        return findAllEntities().stream()
                .map(ResourceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findBySiteId(String siteId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<ResourceEntity> list = em.createQuery(
                    "SELECT r FROM ResourceEntity r WHERE r.siteId = :siteId", ResourceEntity.class)
                    .setParameter("siteId", siteId)
                    .getResultList();
            return list.stream().map(ResourceMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public List<Resource> findByBuildingId(String buildingId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<ResourceEntity> list = em.createQuery(
                    "SELECT r FROM ResourceEntity r WHERE r.building.id = :bldId", ResourceEntity.class)
                    .setParameter("bldId", buildingId)
                    .getResultList();
            return list.stream().map(ResourceMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Resource resource) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            BuildingEntity bldRef = resource.getBuildingId() != null ? em.find(BuildingEntity.class, resource.getBuildingId()) : null;
            ResourceEntity entity = ResourceMapper.toEntity(resource, bldRef);
            saveEntity(entity);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void delete(String id) {
        deleteEntity(id);
    }
}
