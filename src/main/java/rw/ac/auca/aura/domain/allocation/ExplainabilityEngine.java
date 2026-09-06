package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.academic.AcademicActivity;
import rw.ac.auca.aura.domain.shared.ConstraintResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExplainabilityEngine formats transparent audit evidence explaining why a specific resource was selected
 * and why alternative candidate resources were rejected.
 */
public class ExplainabilityEngine {

    public AllocationDecision createDecision(AcademicActivity activity, List<CandidateEvaluation> evaluations, String policyVersion) {
        if (activity == null || evaluations == null || evaluations.isEmpty()) {
            return new AllocationDecision(
                    activity != null ? activity.getId() : "UNKNOWN",
                    "", null, "", false, 0.0, null,
                    List.of(ConstraintResult.fail("NoCandidates", "No candidate resources were available for evaluation")),
                    List.of(), "No candidates available for evaluation."
            );
        }

        List<CandidateEvaluation> feasibleCandidates = evaluations.stream()
                .filter(CandidateEvaluation::isFeasible)
                .collect(Collectors.toList());

        List<CandidateEvaluation> rejectedCandidates = evaluations.stream()
                .filter(eval -> !eval.isFeasible())
                .collect(Collectors.toList());

        if (feasibleCandidates.isEmpty()) {
            // Failure diagnostic explanation
            List<String> bottleneckReasons = new ArrayList<>();
            for (CandidateEvaluation rejected : rejectedCandidates) {
                List<String> failedRules = rejected.getConstraintResults().stream()
                        .filter(res -> !res.isPassed())
                        .map(res -> res.getConstraintName() + ": " + res.getDetailMessage())
                        .collect(Collectors.toList());

                bottleneckReasons.add(String.format("Resource '%s' @ %s rejected -> %s",
                        rejected.getResource().getName(), rejected.getTimeSlot(), String.join("; ", failedRules)));
            }

            String explanation = "INFEASIBLE: No valid resource assignment exists satisfying all hard constraints.\n" +
                    "Evaluated " + rejectedCandidates.size() + " rejected alternatives.\n" +
                    "Primary Bottlenecks:\n" + String.join("\n", bottleneckReasons.stream().limit(5).collect(Collectors.toList()));

            return new AllocationDecision(
                    activity.getId(), "", null, activity.getPreferredSiteId(),
                    false, 0.0, null,
                    rejectedCandidates.isEmpty() ? List.of() : rejectedCandidates.get(0).getConstraintResults(),
                    bottleneckReasons, explanation
            );
        }

        // FEASIBLE recommendation
        CandidateEvaluation bestCandidate = feasibleCandidates.get(0);

        List<String> alternatives = new ArrayList<>();
        for (int i = 1; i < feasibleCandidates.size(); i++) {
            CandidateEvaluation alt = feasibleCandidates.get(i);
            alternatives.add(String.format("Feasible Alternative '%s' @ %s (Score: %.1f)",
                    alt.getResource().getName(), alt.getTimeSlot(), alt.getScore()));
        }
        for (CandidateEvaluation rej : rejectedCandidates) {
            String failDetail = rej.getConstraintResults().stream()
                    .filter(r -> !r.isPassed())
                    .map(ConstraintResult::getDetailMessage)
                    .findFirst().orElse("Failed hard constraint");

            alternatives.add(String.format("Rejected '%s' @ %s -> %s",
                    rej.getResource().getName(), rej.getTimeSlot(), failDetail));
        }

        StringBuilder explanation = new StringBuilder();
        explanation.append(String.format("RECOMMENDED: Resource '%s' at %s (Campus Site: %s) with Utility Score %.1f/100.0\n",
                bestCandidate.getResource().getName(), bestCandidate.getTimeSlot(), bestCandidate.getResource().getSiteId(), bestCandidate.getScore()));
        explanation.append("Satisfied Hard Constraints:\n");
        for (ConstraintResult pass : bestCandidate.getConstraintResults()) {
            explanation.append("  [PASS] ").append(pass.getConstraintName()).append(": ").append(pass.getDetailMessage()).append("\n");
        }
        explanation.append("Soft Score Breakdown: ").append(bestCandidate.getScoreBreakdown().toString()).append("\n");

        return new AllocationDecision(
                activity.getId(),
                bestCandidate.getResource().getId(),
                bestCandidate.getTimeSlot(),
                bestCandidate.getResource().getSiteId(),
                true,
                bestCandidate.getScore(),
                bestCandidate.getScoreBreakdown(),
                bestCandidate.getConstraintResults(),
                alternatives,
                explanation.toString()
        );
    }
}
