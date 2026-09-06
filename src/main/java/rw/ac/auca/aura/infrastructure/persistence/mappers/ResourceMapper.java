package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.shared.Capability;
import rw.ac.auca.aura.domain.shared.CapabilitySet;
import rw.ac.auca.aura.infrastructure.persistence.entities.BuildingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ResourceEntity;

import java.util.EnumSet;
import java.util.Set;

public class ResourceMapper {

    public static Resource toDomain(ResourceEntity entity) {
        if (entity == null) return null;

        String buildingId = entity.getBuilding() != null ? entity.getBuilding().getId() : null;
        CapabilitySet capSet = parseCapabilityJson(entity.getCapabilitiesJson());

        return new Resource(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getCapacity(),
                entity.getSiteId(),
                buildingId,
                capSet,
                entity.getStatus()
        );
    }

    public static ResourceEntity toEntity(Resource domain, BuildingEntity buildingEntity) {
        if (domain == null) return null;

        String capJson = serializeCapabilitySet(domain.getCapabilities());
        String siteId = domain.getSiteId();
        if (siteId == null && buildingEntity != null && buildingEntity.getSite() != null) {
            siteId = buildingEntity.getSite().getId();
        }

        return new ResourceEntity(
                domain.getId(),
                domain.getName(),
                domain.getType(),
                domain.getCapacity(),
                buildingEntity,
                siteId,
                domain.getStatus(),
                capJson
        );
    }

    public static CapabilitySet parseCapabilityJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return CapabilitySet.empty();
        }
        EnumSet<Capability> caps = EnumSet.noneOf(Capability.class);
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "").trim();
        if (!cleaned.isEmpty()) {
            for (String part : cleaned.split(",")) {
                try {
                    caps.add(Capability.valueOf(part.trim()));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return new CapabilitySet(caps);
    }

    public static String serializeCapabilitySet(CapabilitySet capabilitySet) {
        if (capabilitySet == null || capabilitySet.getCapabilities().isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Capability cap : capabilitySet.getCapabilities()) {
            if (!first) sb.append(",");
            sb.append("\"").append(cap.name()).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
