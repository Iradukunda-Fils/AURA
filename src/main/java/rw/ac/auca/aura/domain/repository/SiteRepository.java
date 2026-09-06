package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.resource.Site;

import java.util.List;
import java.util.Optional;

public interface SiteRepository {
    Optional<Site> findById(String id);
    List<Site> findAll();
    void save(Site site);
    void delete(String id);
}
