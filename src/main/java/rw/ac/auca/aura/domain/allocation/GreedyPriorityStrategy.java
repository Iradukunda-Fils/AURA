package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.constraint.CompositeAndSpecification;
import rw.ac.auca.aura.domain.constraint.SpecificationContext;
import rw.ac.auca.aura.domain.policy.AllocationPolicy;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Baseline deterministic Greedy Priority Allocation Strategy.
 * Filters hard constraints, scores candidates using policy weights, selects highest scoring feasible candidate,
 * and maintains transient run state to prevent collisions within a batch.
 */
public class GreedyPriorityStrategy implements AllocationStrategy {

    private final CandidateGenerator candidateGenerator;
    private final ExplainabilityEngine explainabilityEngine;

    public GreedyPriorityStrategy() {
        this.candidateGenerator = new CandidateGenerator(CompositeAndSpecification.defaultHardConstraints(), new ScoringEngine());
        this.explainabilityEngine = new ExplainabilityEngine();
    }

    public GreedyPriorityStrategy(CandidateGenerator candidateGenerator, ExplainabilityEngine explainabilityEngine) {
        this.candidateGenerator = candidateGenerator != null ? candidateGenerator : new CandidateGenerator(CompositeAndSpecification.defaultHardConstraints(), new ScoringEngine());
        this.explainabilityEngine = explainabilityEngine != null ? explainabilityEngine : new ExplainabilityEngine();
    }

    @Override
    public AllocationRun executeAllocationRun(String runId, List<AcademicActivity> activities,
                                               List<Resource> availableResources, List<TimeSlot> candidateTimeSlots,
                                               SpecificationContext baseContext, AllocationPolicy policy, String executedBy) {

        AllocationRun run = new AllocationRun(
                runId,
                policy != null ? policy.getVersion() : "v1.0",
                "GreedyPriority-v1",
                LocalDateTime.now(),
                executedBy != null ? executedBy : "SYSTEM",
                new ArrayList<>(),
                0.0,
                AllocationRun.RunStatus.RUNNING
        );

        if (activities == null || activities.isEmpty()) {
            run.complete(0.0);
            return run;
        }

        List<Allocation> runningAllocations = new ArrayList<>(baseContext != null ? baseContext.getActiveAllocations() : Collections.emptyList());
        List<AcademicActivity> runningActivities = new ArrayList<>(baseContext != null ? baseContext.getActiveActivities() : Collections.emptyList());
        runningActivities.addAll(activities);

        double totalUtilitySum = 0.0;
        int feasibleCount = 0;

        for (AcademicActivity activity : activities) {
            SpecificationContext currentContext = new SpecificationContext(runningAllocations, runningActivities);

            List<CandidateEvaluation> evaluations = candidateGenerator.generateEvaluations(
                    activity,
                    availableResources,
                    candidateTimeSlots,
                    currentContext,
                    policy != null ? policy.getWeights() : null
            );

            AllocationDecision decision = explainabilityEngine.createDecision(activity, evaluations, policy != null ? policy.getVersion() : "v1.0");
            run.addDecision(decision);

            if (decision.isFeasible()) {
                feasibleCount++;
                totalUtilitySum += decision.getScore();

                // Add transient allocation to context for subsequent activities in this run
                Allocation transientAlloc = new Allocation(
                        "RUN-TEMP-" + activity.getId(),
                        runId,
                        activity.getId(),
                        Set.of(decision.getSelectedResourceId()),
                        decision.getSelectedTimeSlot(),
                        decision.getSelectedSiteId(),
                        LocalDateTime.now(),
                        executedBy,
                        Allocation.AllocationStatus.COMMITTED
                );
                runningAllocations.add(transientAlloc);
            }
        }

        double averageGlobalScore = feasibleCount > 0 ? (totalUtilitySum / (double) activities.size()) : 0.0;
        run.complete(averageGlobalScore);
        return run;
    }
}
