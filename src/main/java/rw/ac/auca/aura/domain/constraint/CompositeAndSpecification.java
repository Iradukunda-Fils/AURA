package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Composite Specification evaluating a list of ResourceSpecifications.
 * Passes only if ALL component specifications pass.
 */
public class CompositeAndSpecification implements ResourceSpecification {

    private final List<ResourceSpecification> specifications;

    public CompositeAndSpecification(List<ResourceSpecification> specifications) {
        this.specifications = specifications != null ? new ArrayList<>(specifications) : new ArrayList<>();
    }

    public static CompositeAndSpecification defaultHardConstraints() {
        return new CompositeAndSpecification(Arrays.asList(
                new CapacitySpecification(),
                new CapabilitySpecification(),
                new ResourceAvailabilitySpecification(),
                new LecturerConflictSpecification(),
                new CohortConflictSpecification(),
                new SiteCompatibilitySpecification()
        ));
    }

    public List<ResourceSpecification> getSpecifications() {
        return Collections.unmodifiableList(specifications);
    }

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        for (ResourceSpecification spec : specifications) {
            ConstraintResult result = spec.evaluate(activity, resource, timeSlot, context);
            if (!result.isPassed()) {
                return result; // Short-circuit on first failing hard constraint
            }
        }
        return ConstraintResult.pass("CompositeHardConstraint", "All hard constraints satisfied");
    }

    /**
     * Evaluates all specs without short-circuiting to collect complete diagnostic evidence.
     */
    public List<ConstraintResult> evaluateAll(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        List<ConstraintResult> results = new ArrayList<>();
        for (ResourceSpecification spec : specifications) {
            results.add(spec.evaluate(activity, resource, timeSlot, context));
        }
        return results;
    }
}
