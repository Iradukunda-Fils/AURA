package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_buildings")
public class BuildingEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false, foreignKey = @ForeignKey(name = "fk_building_site"))
    private SiteEntity site;

    public BuildingEntity() {}

    public BuildingEntity(String id, String name, SiteEntity site) {
        this.id = id;
        this.name = name;
        this.site = site;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SiteEntity getSite() { return site; }
    public void setSite(SiteEntity site) { this.site = site; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingEntity that = (BuildingEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
