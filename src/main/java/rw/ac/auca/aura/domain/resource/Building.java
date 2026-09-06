package rw.ac.auca.aura.domain.resource;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain Entity representing a physical building on a campus site.
 */
public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String siteId;

    public Building(String id, String name, String siteId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Building ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Building name cannot be empty");
        }
        if (siteId == null || siteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Site ID cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.siteId = siteId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSiteId() { return siteId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(id, building.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " [" + siteId + "]";
    }
}
