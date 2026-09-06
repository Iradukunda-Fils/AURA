package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.resource.Building;
import rw.ac.auca.aura.infrastructure.persistence.entities.BuildingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.SiteEntity;

public class BuildingMapper {

    public static Building toDomain(BuildingEntity entity) {
        if (entity == null) return null;
        String siteId = entity.getSite() != null ? entity.getSite().getId() : null;
        return new Building(entity.getId(), entity.getName(), siteId);
    }

    public static BuildingEntity toEntity(Building domain, SiteEntity siteEntity) {
        if (domain == null) return null;
        return new BuildingEntity(domain.getId(), domain.getName(), siteEntity);
    }
}
