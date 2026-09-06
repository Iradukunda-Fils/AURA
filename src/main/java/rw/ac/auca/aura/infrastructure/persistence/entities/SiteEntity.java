package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_sites")
public class SiteEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    public SiteEntity() {}

    public SiteEntity(String id, String name, String locationCode) {
        this.id = id;
        this.name = name;
        this.locationCode = locationCode;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEntity that = (SiteEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
