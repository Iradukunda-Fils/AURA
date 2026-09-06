package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

/**
 * Hard Constraint Specification: Resource capacity must be greater than or equal to activity student count.
 */
public class CapacitySpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (activity == null || resource == null) {
            return ConstraintResult.fail("CapacityConstraint", "Invalid activity or resource input");
        }
        int required = activity.getStudentCount();
        int available = resource.getCapacity();

        if (available >= required) {
            return ConstraintResult.pass("CapacityConstraint",
                    String.format("Resource capacity (%d) satisfies demand (%d students)", available, required));
        } else {
            return ConstraintResult.fail("CapacityConstraint",
                    String.format("Insufficient capacity (%d seats available < %d students requested)", available, required));
        }
    }
}
