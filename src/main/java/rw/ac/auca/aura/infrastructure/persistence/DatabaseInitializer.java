package rw.ac.auca.aura.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rw.ac.auca.aura.infrastructure.persistence.entities.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Idempotent Database Initializer for reference data, baseline policies, and system metadata.
 * Uses aura_system_metadata (seed_version) to prevent duplicate execution.
 */
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    public static final String SEED_VERSION_KEY = "seed_version";
    public static final String CURRENT_SEED_VERSION = "1.0";

    public static synchronized boolean initializeDatabaseIfNeeded() {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JpaUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            SystemMetadataEntity metadata = em.find(SystemMetadataEntity.class, SEED_VERSION_KEY);
            if (metadata != null && CURRENT_SEED_VERSION.equals(metadata.getValue())) {
                LOGGER.info("Database seed version " + CURRENT_SEED_VERSION + " already initialized. Skipping.");
                tx.commit();
                return false;
            }

            LOGGER.info("Executing idempotent database reference data initialization (v" + CURRENT_SEED_VERSION + ")...");

            // 1. Sites
            SiteEntity masoro = new SiteEntity("SITE_MAS", "Masoro Main Campus", "Kigali - Masoro");
            SiteEntity gishushu = new SiteEntity("SITE_GIS", "Gishushu Campus", "Kigali - Gishushu");
            em.merge(masoro);
            em.merge(gishushu);

            // 2. Buildings
            BuildingEntity sciBld = new BuildingEntity("BLD_SCI", "Science & Tech Complex", masoro);
            BuildingEntity admBld = new BuildingEntity("BLD_ADM", "Administration Building", masoro);
            BuildingEntity theoBld = new BuildingEntity("BLD_THEO", "Theology & Arts Center", gishushu);
            em.merge(sciBld);
            em.merge(admBld);
            em.merge(theoBld);

            // 3. Baseline Policy
            PolicyEntity defaultPolicy = new PolicyEntity(
                    "POL_DEFAULT",
                    "v1.0",
                    "AUCA Standard Baseline Allocation Policy",
                    25.0, 20.0, 25.0, 15.0, 15.0,
                    true
            );
            em.merge(defaultPolicy);

            // 4. Update System Metadata
            SystemMetadataEntity newMetadata = new SystemMetadataEntity(SEED_VERSION_KEY, CURRENT_SEED_VERSION, LocalDateTime.now());
            em.merge(newMetadata);

            tx.commit();
            LOGGER.info("Database seed initialization completed successfully.");
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOGGER.severe("Database seed initialization failed: " + e.getMessage());
            return false;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
}
