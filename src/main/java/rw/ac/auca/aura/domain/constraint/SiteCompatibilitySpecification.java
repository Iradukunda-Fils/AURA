package rw.ac.auca.aura.domain.constraint;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

/**
 * Hard Constraint Specification: Selected resource must physically belong to the activity's required/preferred site.
 */
public class SiteCompatibilitySpecification implements ResourceSpecification {

    @Override
    public ConstraintResult evaluate(AcademicActivity activity, Resource resource, TimeSlot timeSlot, SpecificationContext context) {
        if (activity == null || resource == null) {
            return ConstraintResult.fail("SiteCompatibilityConstraint", "Activity or Resource is null");
        }

        String requiredSite = activity.getPreferredSiteId();
        if (requiredSite == null || requiredSite.trim().isEmpty()) {
            return ConstraintResult.pass("SiteCompatibilityConstraint", "No site restriction specified");
        }

        if (requiredSite.equalsIgnoreCase(resource.getSiteId())) {
            return ConstraintResult.pass("SiteCompatibilityConstraint",
                    String.format("Resource %s belongs to requested campus site (%s)", resource.getName(), requiredSite));
        } else {
            return ConstraintResult.fail("SiteCompatibilityConstraint",
                    String.format("Site mismatch: Resource %s is located at %s, but activity requires site %s",
                            resource.getName(), resource.getSiteId(), requiredSite));
        }
    }
}
