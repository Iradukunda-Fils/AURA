package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.resource.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    Optional<Resource> findById(String id);
    List<Resource> findAll();
    List<Resource> findBySiteId(String siteId);
    List<Resource> findByBuildingId(String buildingId);
    void save(Resource resource);
    void delete(String id);
}
