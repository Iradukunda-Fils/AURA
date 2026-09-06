package rw.ac.auca.aura.domain.resource;

import rw.ac.auca.aura.domain.shared.CapabilitySet;

import java.io.Serializable;
import java.util.Objects;

/**
 * Aggregate Root representing an allocatable physical institutional resource (room, lab, equipment).
 */
public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final ResourceType type;
    private final int capacity;
    private final String siteId;
    private final String buildingId;
    private final CapabilitySet capabilities;
    private ResourceStatus status;

    public Resource(String id, String name, ResourceType type, int capacity,
                    String siteId, String buildingId, CapabilitySet capabilities, ResourceStatus status) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        if (siteId == null || siteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Site ID cannot be empty");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.siteId = siteId;
        this.buildingId = buildingId != null ? buildingId : "";
        this.capabilities = capabilities != null ? capabilities : CapabilitySet.empty();
        this.status = status != null ? status : ResourceStatus.ACTIVE;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ResourceType getType() { return type; }
    public int getCapacity() { return capacity; }
    public String getSiteId() { return siteId; }
    public String getBuildingId() { return buildingId; }
    public CapabilitySet getCapabilities() { return capabilities; }
    public ResourceStatus getStatus() { return status; }

    public boolean isOperational() {
        return status == ResourceStatus.ACTIVE;
    }

    public void updateStatus(ResourceStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + type + ", Cap: " + capacity + ", Site: " + siteId + ")";
    }
}
