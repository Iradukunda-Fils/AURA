package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.constraint.CompositeAndSpecification;
import rw.ac.auca.aura.domain.constraint.SpecificationContext;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;
import rw.ac.auca.aura.domain.resource.Resource;
import rw.ac.auca.aura.domain.scheduling.TimeSlot;
import rw.ac.auca.aura.domain.shared.ConstraintResult;
import rw.ac.auca.aura.domain.shared.ScoreBreakdown;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CandidateGenerator generates candidate (Resource, TimeSlot) evaluation pairs for an AcademicActivity.
 * Applies hard-constraint pruning before scoring.
 */
public class CandidateGenerator {

    private final CompositeAndSpecification hardConstraints;
    private final ScoringEngine scoringEngine;

    public CandidateGenerator(CompositeAndSpecification hardConstraints, ScoringEngine scoringEngine) {
        this.hardConstraints = hardConstraints != null ? hardConstraints : CompositeAndSpecification.defaultHardConstraints();
        this.scoringEngine = scoringEngine != null ? scoringEngine : new ScoringEngine();
    }

    public List<CandidateEvaluation> generateEvaluations(AcademicActivity activity,
                                                          List<Resource> availableResources,
                                                          List<TimeSlot> candidateTimeSlots,
                                                          SpecificationContext context,
                                                          ScoringWeightPolicy policy) {
        List<CandidateEvaluation> evaluations = new ArrayList<>();
        if (activity == null || availableResources == null || availableResources.isEmpty()) {
            return evaluations;
        }

        List<TimeSlot> slotsToEvaluate = candidateTimeSlots;
        if (slotsToEvaluate == null || slotsToEvaluate.isEmpty()) {
            if (activity.getPreferredTimeSlot() != null) {
                slotsToEvaluate = List.of(activity.getPreferredTimeSlot());
            } else {
                return evaluations;
            }
        }

        for (Resource resource : availableResources) {
            for (TimeSlot timeSlot : slotsToEvaluate) {
                List<ConstraintResult> constraintResults = hardConstraints.evaluateAll(activity, resource, timeSlot, context);
                boolean feasible = constraintResults.stream().allMatch(ConstraintResult::isPassed);

                ScoreBreakdown scoreBreakdown = null;
                if (feasible) {
                    scoreBreakdown = scoringEngine.calculateScore(activity, resource, timeSlot, policy);
                }

                evaluations.add(new CandidateEvaluation(resource, timeSlot, feasible, constraintResults, scoreBreakdown));
            }
        }

        // Sort feasible candidates descending by score
        evaluations.sort(Comparator.comparing(CandidateEvaluation::isFeasible).reversed()
                .thenComparing(Comparator.comparing(CandidateEvaluation::getScore).reversed()));

        return evaluations;
    }
}
