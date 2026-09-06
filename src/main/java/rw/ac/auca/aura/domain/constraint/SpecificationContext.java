package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.allocation.Allocation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Context container supplied during constraint evaluation containing live committed allocations
 * and active activities for conflict checking.
 */
public final class SpecificationContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Allocation> activeAllocations;
    private final List<AcademicActivity> activeActivities;

    public SpecificationContext(List<Allocation> activeAllocations, List<AcademicActivity> activeActivities) {
        this.activeAllocations = activeAllocations != null ? Collections.unmodifiableList(activeAllocations) : Collections.emptyList();
        this.activeActivities = activeActivities != null ? Collections.unmodifiableList(activeActivities) : Collections.emptyList();
    }

    public static SpecificationContext empty() {
        return new SpecificationContext(Collections.emptyList(), Collections.emptyList());
    }

    public List<Allocation> getActiveAllocations() {
        return activeAllocations;
    }

    public List<AcademicActivity> getActiveActivities() {
        return activeActivities;
    }

    public AcademicActivity findActivityById(String activityId) {
        if (activityId == null) return null;
        for (AcademicActivity activity : activeActivities) {
            if (Objects.equals(activity.getId(), activityId)) {
                return activity;
            }
        }
        return null;
    }
}
