package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.CourseOffering;

import java.util.List;
import java.util.Optional;

public interface CourseOfferingRepository {
    Optional<CourseOffering> findById(String id);
    List<CourseOffering> findAll();
    List<CourseOffering> findByCourseId(String courseId);
    void save(CourseOffering offering);
    void delete(String id);
}
