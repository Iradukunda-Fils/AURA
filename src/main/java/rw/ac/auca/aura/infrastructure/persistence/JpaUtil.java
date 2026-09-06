package rw.ac.auca.aura.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton utility to provide JPA EntityManager instances for local database transactions.
 * Supports environment variable overrides for database host and port (e.g. localhost fallback when outside Docker).
 */
public class JpaUtil {

    private static final String PERSISTENCE_UNIT_NAME = "default";
    private static EntityManagerFactory emf;

    static {
        try {
            Map<String, String> configOverrides = new HashMap<>();
            
            String dbHost = System.getenv("DB_HOST");
            if (dbHost == null || dbHost.isBlank()) {
                dbHost = "postgres"; // Docker network default service name
            }
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "auradb");
            String dbUser = System.getenv().getOrDefault("DB_USER", "aura");
            String dbPass = System.getenv().getOrDefault("DB_PASS", "aurapassword");

            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
            configOverrides.put("jakarta.persistence.jdbc.url", jdbcUrl);
            configOverrides.put("jakarta.persistence.jdbc.user", dbUser);
            configOverrides.put("jakarta.persistence.jdbc.password", dbPass);

            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, configOverrides);
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed: " + ex);
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory failed to initialize");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
