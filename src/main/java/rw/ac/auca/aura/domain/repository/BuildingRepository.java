package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.resource.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository {
    Optional<Building> findById(String id);
    List<Building> findAll();
    List<Building> findBySiteId(String siteId);
    void save(Building building);
    void delete(String id);
}
