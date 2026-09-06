package rw.ac.auca.aura.domain.repository;

import rw.ac.auca.aura.domain.academic.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> findById(String id);
    Optional<Student> findByStudentNumber(String studentNumber);
    List<Student> findAll();
    List<Student> findByCohortId(String cohortId);
    void save(Student student);
    void delete(String id);
}
