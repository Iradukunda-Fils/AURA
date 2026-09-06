package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.AcademicActivity;

import java.util.List;
import java.util.Optional;

public interface AcademicActivityRepository {
    Optional<AcademicActivity> findById(String id);
    List<AcademicActivity> findAll();
    List<AcademicActivity> findByOfferingId(String offeringId);
    void save(AcademicActivity activity);
    void delete(String id);
}
