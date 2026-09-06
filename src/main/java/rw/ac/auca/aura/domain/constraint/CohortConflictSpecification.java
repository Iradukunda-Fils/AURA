package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

import java.util.Set;

/**
 * Hard Constraint Specification: Target student cohorts cannot be scheduled for overlapping activities.
 */
public class CohortConflictSpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (activity == null || timeSlot == null) {
            return ConstraintResult.fail("CohortConflictConstraint", "Activity or TimeSlot is null");
        }

        Set<String> targetCohorts = activity.getCohortIds();
        if (targetCohorts.isEmpty() || context == null) {
            return ConstraintResult.pass("CohortConflictConstraint", "No student cohort restrictions apply");
        }

        for (Allocation alloc : context.getActiveAllocations()) {
            if (alloc.isCommitted() && alloc.getTimeSlot().overlaps(timeSlot)) {
                AcademicActivity existingActivity = context.findActivityById(alloc.getActivityId());
                if (existingActivity != null) {
                    for (String cohortId : targetCohorts) {
                        if (existingActivity.getCohortIds().contains(cohortId)) {
                            return ConstraintResult.fail("CohortConflictConstraint",
                                    String.format("Student Cohort %s has an overlapping class (%s) scheduled during %s",
                                            cohortId, existingActivity.getTitle(), timeSlot));
                        }
                    }
                }
            }
        }

        return ConstraintResult.pass("CohortConflictConstraint", "All target student cohorts are conflict-free at " + timeSlot);
    }
}
