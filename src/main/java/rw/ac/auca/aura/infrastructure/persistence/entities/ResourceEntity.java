package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import rw.ac.auca.aura.domain.resource.ResourceStatus;
import rw.ac.auca.aura.domain.resource.ResourceType;
import java.util.Objects;

@Entity
@Table(name = "aura_resources")
public class ResourceEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceType type;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false, foreignKey = @ForeignKey(name = "fk_resource_building"))
    private BuildingEntity building;

    @Column(name = "site_id", nullable = false, length = 50)
    private String siteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceStatus status;

    @Column(name = "capabilities_json", length = 2000)
    private String capabilitiesJson;

    public ResourceEntity() {}

    public ResourceEntity(String id, String name, ResourceType type, int capacity, BuildingEntity building, String siteId, ResourceStatus status, String capabilitiesJson) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.building = building;
        this.siteId = siteId;
        this.status = status;
        this.capabilitiesJson = capabilitiesJson;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public BuildingEntity getBuilding() { return building; }
    public void setBuilding(BuildingEntity building) { this.building = building; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    public String getCapabilitiesJson() { return capabilitiesJson; }
    public void setCapabilitiesJson(String capabilitiesJson) { this.capabilitiesJson = capabilitiesJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceEntity that = (ResourceEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
