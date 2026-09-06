package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.Program;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository {
    Optional<Program> findById(String id);
    List<Program> findAll();
    List<Program> findByDepartmentId(String departmentId);
    void save(Program program);
    void delete(String id);
}
