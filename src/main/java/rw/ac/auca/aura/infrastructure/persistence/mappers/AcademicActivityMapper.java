package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.CapabilitySet;
import rw.ac.auca.aura.infrastructure.persistence.entities.AcademicActivityEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.CourseOfferingEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.LecturerEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.StudentCohortEntity;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Collectors;

public class AcademicActivityMapper {

    public static AcademicActivity toDomain(AcademicActivityEntity entity) {
        if (entity == null) return null;

        String offeringId = entity.getOffering() != null ? entity.getOffering().getId() : null;

        Set<String> lecturerIds = entity.getLecturers().stream()
                .map(LecturerEntity::getId)
                .collect(Collectors.toSet());

        Set<String> cohortIds = entity.getCohorts().stream()
                .map(StudentCohortEntity::getId)
                .collect(Collectors.toSet());

        CapabilitySet reqCaps = ResourceMapper.parseCapabilityJson(entity.getRequiredCapabilitiesJson());

        TimeSlot preferredTimeSlot = null;
        if (entity.getPreferredDayOfWeek() != null && entity.getPreferredStartTime() != null && entity.getPreferredEndTime() != null) {
            preferredTimeSlot = new TimeSlot(
                    DayOfWeek.valueOf(entity.getPreferredDayOfWeek().toUpperCase()),
                    entity.getPreferredStartTime(),
                    entity.getPreferredEndTime()
            );
        }

        return new AcademicActivity(
                entity.getId(),
                offeringId,
                entity.getTitle(),
                entity.getActivityType(),
                lecturerIds,
                cohortIds,
                entity.getStudentCount(),
                entity.getDurationMinutes(),
                reqCaps,
                entity.getPreferredSiteId(),
                preferredTimeSlot,
                entity.getState()
        );
    }

    public static AcademicActivityEntity toEntity(AcademicActivity domain, CourseOfferingEntity offeringEntity, Set<LecturerEntity> lecturers, Set<StudentCohortEntity> cohorts) {
        if (domain == null) return null;

        String reqCapsJson = ResourceMapper.serializeCapabilitySet(domain.getRequiredCapabilities());
        String dayOfWeek = domain.getPreferredTimeSlot() != null ? domain.getPreferredTimeSlot().getDayOfWeek().name() : "MONDAY";
        java.time.LocalTime start = domain.getPreferredTimeSlot() != null ? domain.getPreferredTimeSlot().getStartTime() : java.time.LocalTime.of(8, 0);
        java.time.LocalTime end = domain.getPreferredTimeSlot() != null ? domain.getPreferredTimeSlot().getEndTime() : java.time.LocalTime.of(10, 0);

        AcademicActivityEntity entity = new AcademicActivityEntity(
                domain.getId(),
                offeringEntity,
                domain.getTitle(),
                domain.getActivityType(),
                domain.getStudentCount(),
                (int) domain.getDurationMinutes(),
                reqCapsJson,
                domain.getPreferredSiteId(),
                dayOfWeek,
                start,
                end,
                domain.getState()
        );

        if (lecturers != null) entity.setLecturers(lecturers);
        if (cohorts != null) entity.setCohorts(cohorts);

        return entity;
    }
}
