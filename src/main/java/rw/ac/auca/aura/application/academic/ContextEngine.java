package rw.ac.auca.aura.application.academic;

import rw.ac.auca.aura.domain.academic.*;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.Capability;
import rw.ac.auca.aura.domain.shared.CapabilitySet;

import java.util.Objects;
import java.util.Set;

/**
 * Application context engine responsible for resolving, validating, and converting
 * high-level academic intents into canonical AcademicActivity domain aggregates.
 */
public class ContextEngine {

    /**
     * Resolves a course offering, activity type, and preferred time slot into an AcademicActivity.
     * Enforces required capability inheritance (e.g. LABORATORY activity implies COMPUTERS / PROJECTOR requirements).
     */
    public AcademicActivity createActivity(
            String activityId,
            CourseOffering offering,
            Course course,
            ActivityType type,
            TimeSlot preferredTimeSlot,
            Set<String> lecturerIds,
            Set<String> cohortIds,
            int studentCount,
            String preferredSiteId) {

        Objects.requireNonNull(activityId, "Activity ID must not be null");
        Objects.requireNonNull(offering, "Course offering must not be null");
        Objects.requireNonNull(type, "Activity type must not be null");

        // Inherit base requirements based on activity type
        CapabilitySet mergedRequirements;
        
        switch (type) {
            case LABORATORY -> mergedRequirements = CapabilitySet.of(Capability.COMPUTERS, Capability.PROJECTOR, Capability.AIR_CONDITIONING);
            case LECTURE -> mergedRequirements = CapabilitySet.of(Capability.PROJECTOR, Capability.AUDIO_SYSTEM, Capability.WHITEBOARD);
            case SEMINAR -> mergedRequirements = CapabilitySet.of(Capability.WHITEBOARD, Capability.PROJECTOR);
            case EXAMINATION -> mergedRequirements = CapabilitySet.of(Capability.PROJECTOR);
            default -> mergedRequirements = CapabilitySet.empty();
        }

        return new AcademicActivity(
                activityId,
                offering.getId(),
                course != null ? course.getCode() + " - " + course.getName() : "Activity " + activityId,
                type,
                lecturerIds != null ? lecturerIds : offering.getLecturerIds(),
                cohortIds != null ? cohortIds : offering.getCohortIds(),
                studentCount > 0 ? studentCount : 40,
                preferredTimeSlot != null ? preferredTimeSlot.getDurationInMinutes() : 120,
                mergedRequirements,
                preferredSiteId != null ? preferredSiteId : "",
                preferredTimeSlot,
                ActivityState.SUBMITTED
        );
    }
}
