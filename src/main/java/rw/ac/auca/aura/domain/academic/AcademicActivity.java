package rw.ac.auca.aura.domain.academic;

import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.CapabilitySet;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Root representing an operational academic demand unit that must be allocated.
 */
public class AcademicActivity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String offeringId;
    private final String title;
    private final ActivityType activityType;
    private final Set<String> lecturerIds;
    private final Set<String> cohortIds;
    private final int studentCount;
    private final long durationMinutes;
    private final CapabilitySet requiredCapabilities;
    private final String preferredSiteId;
    private final TimeSlot preferredTimeSlot;
    private ActivityState state;

    public AcademicActivity(String id, String offeringId, String title, ActivityType activityType,
                            Set<String> lecturerIds, Set<String> cohortIds, int studentCount,
                            long durationMinutes, CapabilitySet requiredCapabilities,
                            String preferredSiteId, TimeSlot preferredTimeSlot, ActivityState state) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity ID cannot be empty");
        }
        if (offeringId == null || offeringId.trim().isEmpty()) {
            throw new IllegalArgumentException("Offering ID cannot be empty");
        }
        if (activityType == null) {
            throw new IllegalArgumentException("Activity type cannot be null");
        }
        if (studentCount <= 0) {
            throw new IllegalArgumentException("Student count must be positive");
        }

        this.id = id;
        this.offeringId = offeringId;
        this.title = title != null ? title : "Academic Activity " + id;
        this.activityType = activityType;
        this.lecturerIds = lecturerIds != null ? Collections.unmodifiableSet(new HashSet<>(lecturerIds)) : Collections.emptySet();
        this.cohortIds = cohortIds != null ? Collections.unmodifiableSet(new HashSet<>(cohortIds)) : Collections.emptySet();
        this.studentCount = studentCount;
        this.durationMinutes = durationMinutes > 0 ? durationMinutes : 120;
        this.requiredCapabilities = requiredCapabilities != null ? requiredCapabilities : CapabilitySet.empty();
        this.preferredSiteId = preferredSiteId != null ? preferredSiteId : "";
        this.preferredTimeSlot = preferredTimeSlot;
        this.state = state != null ? state : ActivityState.DRAFT;
    }

    public String getId() { return id; }
    public String getOfferingId() { return offeringId; }
    public String getTitle() { return title; }
    public ActivityType getActivityType() { return activityType; }
    public Set<String> getLecturerIds() { return lecturerIds; }
    public Set<String> getCohortIds() { return cohortIds; }
    public int getStudentCount() { return studentCount; }
    public long getDurationMinutes() { return durationMinutes; }
    public CapabilitySet getRequiredCapabilities() { return requiredCapabilities; }
    public String getPreferredSiteId() { return preferredSiteId; }
    public TimeSlot getPreferredTimeSlot() { return preferredTimeSlot; }
    public ActivityState getState() { return state; }

    public void transitionTo(ActivityState newState) {
        if (newState != null) {
            this.state = newState;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicActivity activity = (AcademicActivity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title + " (" + activityType + ", " + studentCount + " students, State: " + state + ")";
    }
}
