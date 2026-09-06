package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.StudentCohort;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;

public class StudentCohortMapper {

    public static StudentCohort toDomain(StudentCohortEntity entity) {
        if (entity == null) return null;
        String programId = entity.getProgram() != null ? entity.getProgram().getId() : null;
        return new StudentCohort(entity.getId(), entity.getName(), programId, entity.getAcademicYear(), entity.getStudentCount());
    }

    public static StudentCohortEntity toEntity(StudentCohort domain, ProgramEntity programEntity) {
        if (domain == null) return null;
        return new StudentCohortEntity(domain.getId(), domain.getName(), programEntity, domain.getAcademicYear(), domain.getStudentCount());
    }
}
