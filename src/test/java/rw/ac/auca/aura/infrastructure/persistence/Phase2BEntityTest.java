package rw.ac.auca.aura.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rw.ac.auca.aura.infrastructure.persistence.entities.BuildingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.SiteEntity;

import static org.junit.jupiter.api.Assertions.*;

public class Phase2BEntityTest {

    @Test
    @DisplayName("SiteEntity construct and getters/setters verify")
    void testSiteEntity() {
        SiteEntity site = new SiteEntity("SITE_MASORO", "Masoro Campus", "KIG_MAS");
        assertEquals("SITE_MASORO", site.getId());
        assertEquals("Masoro Campus", site.getName());
        assertEquals("KIG_MAS", site.getLocationCode());

        site.setName("Masoro Main Campus");
        assertEquals("Masoro Main Campus", site.getName());
    }

    @Test
    @DisplayName("BuildingEntity construct and relationship with SiteEntity verify")
    void testBuildingEntity() {
        SiteEntity site = new SiteEntity("SITE_MASORO", "Masoro Campus", "KIG_MAS");
        BuildingEntity building = new BuildingEntity("BLD_SCI", "Science & Tech Complex", site);

        assertEquals("BLD_SCI", building.getId());
        assertEquals("Science & Tech Complex", building.getName());
        assertNotNull(building.getSite());
        assertEquals("SITE_MASORO", building.getSite().getId());
        assertEquals("Masoro Campus", building.getSite().getName());
    }
}
