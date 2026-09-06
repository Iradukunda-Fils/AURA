package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(String id);
    Optional<Course> findByCode(String code);
    List<Course> findAll();
    List<Course> findByProgramId(String programId);
    void save(Course course);
    void delete(String id);
}
