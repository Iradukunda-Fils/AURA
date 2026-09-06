package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.CapabilitySet;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

/**
 * Hard Constraint Specification: RequiredCapabilities <= ResourceCapabilities.
 */
public class CapabilitySpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (activity == null || resource == null) {
            return ConstraintResult.fail("CapabilityConstraint", "Invalid activity or resource input");
        }

        CapabilitySet required = activity.getRequiredCapabilities();
        CapabilitySet provided = resource.getCapabilities();

        if (provided.satisfies(required)) {
            return ConstraintResult.pass("CapabilityConstraint",
                    "Resource provides all required capabilities: " + required.getCapabilities());
        } else {
            return ConstraintResult.fail("CapabilityConstraint",
                    "Resource missing required capabilities. Provided: " + provided.getCapabilities() +
                            ", Required: " + required.getCapabilities());
        }
    }
}
