package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

/**
 * Hard Constraint Specification: Resource must be active and not allocated to an overlapping activity.
 */
public class ResourceAvailabilitySpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (resource == null || !resource.isOperational()) {
            return ConstraintResult.fail("ResourceAvailabilityConstraint",
                    "Resource " + (resource != null ? resource.getName() : "null") + " is currently inactive or under maintenance");
        }

        if (timeSlot == null) {
            return ConstraintResult.fail("ResourceAvailabilityConstraint", "TimeSlot is null");
        }

        if (context != null && context.getActiveAllocations() != null) {
            for (Allocation alloc : context.getActiveAllocations()) {
                if (alloc.isCommitted() && alloc.getResourceIds().contains(resource.getId())) {
                    if (alloc.getTimeSlot().overlaps(timeSlot)) {
                        return ConstraintResult.fail("ResourceAvailabilityConstraint",
                                String.format("Resource %s is already occupied during %s by Activity #%s",
                                        resource.getName(), timeSlot, alloc.getActivityId()));
                    }
                }
            }
        }

        return ConstraintResult.pass("ResourceAvailabilityConstraint",
                "Resource " + resource.getName() + " is active and available at " + timeSlot);
    }
}
