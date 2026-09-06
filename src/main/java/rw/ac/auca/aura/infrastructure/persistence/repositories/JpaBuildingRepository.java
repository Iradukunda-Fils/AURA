package rw.ac.auca.aura.infrastructure.persistence.repositories;

import jakarta.persistence.EntityManager;
import rw.ac.auca.aura.domain.repository.BuildingRepository;
import rw.ac.auca.aura.domain.resource.Building;
import rw.ac.auca.aura.infrastructure.persistence.JpaUtil;
import rw.ac.auca.aura.infrastructure.persistence.entities.BuildingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.SiteEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.BuildingMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaBuildingRepository extends GenericDao<BuildingEntity, String> implements BuildingRepository {

    public JpaBuildingRepository() {
        super(BuildingEntity.class);
    }

    @Override
    public Optional<Building> findById(String id) {
        return findEntityById(id).map(BuildingMapper::toDomain);
    }

    @Override
    public List<Building> findAll() {
        return findAllEntities().stream()
                .map(BuildingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Building> findBySiteId(String siteId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<BuildingEntity> list = em.createQuery(
                    "SELECT b FROM BuildingEntity b WHERE b.site.id = :siteId", BuildingEntity.class)
                    .setParameter("siteId", siteId)
                    .getResultList();
            return list.stream().map(BuildingMapper::toDomain).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public void save(Building building) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            SiteEntity siteRef = building.getSiteId() != null ? em.find(SiteEntity.class, building.getSiteId()) : null;
            BuildingEntity entity = BuildingMapper.toEntity(building, siteRef);
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
