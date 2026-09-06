package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.StudentCohort;

import java.util.List;
import java.util.Optional;

public interface StudentCohortRepository {
    Optional<StudentCohort> findById(String id);
    List<StudentCohort> findAll();
    List<StudentCohort> findByProgramId(String programId);
    void save(StudentCohort cohort);
    void delete(String id);
}
