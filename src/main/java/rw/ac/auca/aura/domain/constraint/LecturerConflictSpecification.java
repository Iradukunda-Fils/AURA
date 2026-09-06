package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

import java.util.Set;

/**
 * Hard Constraint Specification: Lecturer cannot be assigned to multiple simultaneous activities.
 */
public class LecturerConflictSpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (activity == null || timeSlot == null) {
            return ConstraintResult.fail("LecturerConflictConstraint", "Activity or TimeSlot is null");
        }

        Set<String> targetLecturers = activity.getLecturerIds();
        if (targetLecturers.isEmpty() || context == null) {
            return ConstraintResult.pass("LecturerConflictConstraint", "No lecturer restrictions apply");
        }

        for (Allocation alloc : context.getActiveAllocations()) {
            if (alloc.isCommitted() && alloc.getTimeSlot().overlaps(timeSlot)) {
                AcademicActivity existingActivity = context.findActivityById(alloc.getActivityId());
                if (existingActivity != null) {
                    for (String lecturerId : targetLecturers) {
                        if (existingActivity.getLecturerIds().contains(lecturerId)) {
                            return ConstraintResult.fail("LecturerConflictConstraint",
                                    String.format("Lecturer %s has an overlapping teaching assignment (%s) during %s",
                                            lecturerId, existingActivity.getTitle(), timeSlot));
                        }
                    }
                }
            }
        }

        return ConstraintResult.pass("LecturerConflictConstraint", "All assigned lecturers are conflict-free at " + timeSlot);
    }
}
