package rw.ac.auca.aura.domain.resource;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain Entity representing a physical institutional campus site.
 */
public class Site implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String locationCode;

    public Site(String id, String name, String locationCode) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Site ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Site name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.locationCode = locationCode != null ? locationCode : id;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocationCode() { return locationCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(id, site.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
