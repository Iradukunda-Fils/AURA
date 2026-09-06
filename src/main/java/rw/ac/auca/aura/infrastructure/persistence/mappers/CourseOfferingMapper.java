package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.CourseOffering;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseOfferingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class CourseOfferingMapper {

    public static CourseOffering toDomain(CourseOfferingEntity entity) {
        if (entity == null) return null;

        String courseId = entity.getCourse() != null ? entity.getCourse().getId() : null;
        String term = entity.getAcademicYear() + "-" + entity.getSemester();

        Set<String> lecturerIds = entity.getLecturers().stream()
                .map(LecturerEntity::getId)
                .collect(Collectors.toSet());

        Set<String> cohortIds = entity.getCohorts().stream()
                .map(StudentCohortEntity::getId)
                .collect(Collectors.toSet());

        return new CourseOffering(
                entity.getId(),
                courseId,
                term,
                entity.getSection(),
                lecturerIds,
                cohortIds
        );
    }

    public static CourseOfferingEntity toEntity(CourseOffering domain, CourseEntity courseEntity, Set<LecturerEntity> lecturers, Set<StudentCohortEntity> cohorts) {
        if (domain == null) return null;

        int year = 2026;
        String sem = "S2";
        if (domain.getTerm() != null && domain.getTerm().contains("-")) {
            String[] parts = domain.getTerm().split("-");
            try {
                year = Integer.parseInt(parts[0]);
            } catch (NumberFormatException ignored) {}
            if (parts.length > 1) {
                sem = parts[1];
            }
        }

        CourseOfferingEntity entity = new CourseOfferingEntity(
                domain.getId(),
                courseEntity,
                year,
                sem,
                domain.getSection()
        );

        if (lecturers != null) entity.setLecturers(lecturers);
        if (cohorts != null) entity.setCohorts(cohorts);

        return entity;
    }
}
