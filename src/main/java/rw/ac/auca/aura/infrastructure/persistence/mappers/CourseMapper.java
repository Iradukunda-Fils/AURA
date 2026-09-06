package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.Course;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.ProgramEntity;

public class CourseMapper {

    public static Course toDomain(CourseEntity entity) {
        if (entity == null) return null;
        String programId = entity.getProgram() != null ? entity.getProgram().getId() : null;
        return new Course(entity.getId(), entity.getCode(), entity.getTitle(), entity.getCreditHours(), programId);
    }

    public static CourseEntity toEntity(Course domain, ProgramEntity programEntity) {
        if (domain == null) return null;
        return new CourseEntity(domain.getId(), domain.getCode(), domain.getName(), domain.getCredits(), programEntity);
    }
}
