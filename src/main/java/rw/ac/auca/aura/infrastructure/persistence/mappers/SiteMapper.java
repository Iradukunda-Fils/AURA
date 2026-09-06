package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.resource.Site;
import rw.ac.auca.aura.infrastructure.persistence.entities.SiteEntity;

public class SiteMapper {

    public static Site toDomain(SiteEntity entity) {
        if (entity == null) return null;
        return new Site(entity.getId(), entity.getName(), entity.getLocationCode());
    }

    public static SiteEntity toEntity(Site domain) {
        if (domain == null) return null;
        return new SiteEntity(domain.getId(), domain.getName(), domain.getLocationCode());
    }
}
