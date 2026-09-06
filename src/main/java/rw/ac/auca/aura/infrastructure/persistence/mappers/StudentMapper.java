package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.Student;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentEntity;

public class StudentMapper {

    public static Student toDomain(StudentEntity entity) {
        if (entity == null) return null;
        String cohortId = entity.getCohort() != null ? entity.getCohort().getId() : null;
        return new Student(entity.getId(), entity.getStudentNumber(), entity.getFullName(), entity.getEmail(), cohortId);
    }

    public static StudentEntity toEntity(Student domain, StudentCohortEntity cohortEntity) {
        if (domain == null) return null;
        return new StudentEntity(domain.getId(), domain.getStudentNumber(), domain.getName(), domain.getEmail(), cohortEntity);
    }
}
