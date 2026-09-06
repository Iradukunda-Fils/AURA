package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.Lecturer;

import java.util.List;
import java.util.Optional;

public interface LecturerRepository {
    Optional<Lecturer> findById(String id);
    Optional<Lecturer> findByStaffNumber(String staffNumber);
    List<Lecturer> findAll();
    List<Lecturer> findByDepartmentId(String departmentId);
    void save(Lecturer lecturer);
    void delete(String id);
}
