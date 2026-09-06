package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.constraint.SpecificationContext;
import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;

import java.util.List;

/**
 * Strategy Pattern interface for allocation algorithms (Greedy Priority, Weighted Batch, etc.).
 */
public interface AllocationStrategy {
    AllocationRun executeAllocationRun(
            String runId,
            List<AcademicActivity> activities,
            List<Resource> availableResources,
            List<TimeSlot> candidateTimeSlots,
            SpecificationContext context,
            AllocationPolicy policy,
            String executedBy
    );
}
