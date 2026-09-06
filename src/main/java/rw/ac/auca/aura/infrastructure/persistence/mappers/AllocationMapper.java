package rw.ac.auca.aura.infrastructure.persistence.mappers;

import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationEntity;
import rw.ac.auca.aura.infrastructure.persistence.entities.AllocationResourceEntity;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Collectors;

public class AllocationMapper {

    public static Allocation toDomain(AllocationEntity entity) {
        if (entity == null) return null;

        TimeSlot timeSlot = null;
        if (entity.getDayOfWeek() != null && entity.getStartTime() != null && entity.getEndTime() != null) {
            timeSlot = new TimeSlot(
                    DayOfWeek.valueOf(entity.getDayOfWeek().toUpperCase()),
                    entity.getStartTime(),
                    entity.getEndTime()
            );
        }

        Set<String> resourceIds = entity.getResources().stream()
                .map(AllocationResourceEntity::getResourceId)
                .collect(Collectors.toSet());

        return new Allocation(
                entity.getId(),
                entity.getRunId(),
                entity.getActivityId(),
                resourceIds,
                timeSlot,
                entity.getSiteId(),
                entity.getCommittedAt(),
                entity.getCommittedBy(),
                entity.getStatus()
        );
    }

    public static AllocationEntity toEntity(Allocation domain) {
        if (domain == null) return null;

        String dayOfWeek = domain.getTimeSlot() != null ? domain.getTimeSlot().getDayOfWeek().name() : "MONDAY";
        java.time.LocalTime start = domain.getTimeSlot() != null ? domain.getTimeSlot().getStartTime() : java.time.LocalTime.of(8, 0);
        java.time.LocalTime end = domain.getTimeSlot() != null ? domain.getTimeSlot().getEndTime() : java.time.LocalTime.of(10, 0);

        AllocationEntity entity = new AllocationEntity(
                domain.getId(),
                domain.getRunId(),
                domain.getActivityId(),
                domain.getSiteId(),
                dayOfWeek,
                start,
                end,
                domain.getCommittedAt(),
                domain.getCommittedBy(),
                domain.getStatus()
        );

        if (domain.getResourceIds() != null) {
            for (String resId : domain.getResourceIds()) {
                entity.addResource(resId);
            }
        }

        return entity;
    }
}
