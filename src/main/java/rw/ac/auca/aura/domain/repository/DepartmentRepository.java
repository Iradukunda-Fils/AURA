package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(String id);
    List<Department> findAll();
    void save(Department department);
    void delete(String id);
}
