package rw.ac.auca.aura.infrastructure.persistence.repositories;

import rw.ac.auca.aura.domain.repository.SiteRepository;
import rw.ac.auca.aura.domain.resource.Site;
import rw.ac.auca.aura.infrastructure.persistence.entities.SiteEntity;
import rw.ac.auca.aura.infrastructure.persistence.mappers.SiteMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaSiteRepository extends GenericDao<SiteEntity, String> implements SiteRepository {

    public JpaSiteRepository() {
        super(SiteEntity.class);
    }

    @Override
    public Optional<Site> findById(String id) {
        return findEntityById(id).map(SiteMapper::toDomain);
    }

    @Override
    public List<Site> findAll() {
        return findAllEntities().stream()
                .map(SiteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Site site) {
        SiteEntity entity = SiteMapper.toEntity(site);
        saveEntity(entity);
    }

    @Override
    public void delete(String id) {
        deleteEntity(id);
    }
}
