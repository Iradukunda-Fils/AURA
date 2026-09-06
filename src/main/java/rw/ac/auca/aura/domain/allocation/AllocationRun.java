package rw.ac.auca.aura.domain.allocation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root representing an execution snapshot of an allocation pass.
 * Retains policy versions, strategy version, global scores, decisions, and provenance.
 */
public class AllocationRun implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RunStatus { RUNNING, COMPLETED, FAILED, CANCELLED }

    private final String runId;
    private final String policyVersion;
    private final String strategyVersion;
    private final LocalDateTime executedAt;
    private final String executedBy;
    private final List<AllocationDecision> decisions;
    private double globalUtilityScore;
    private RunStatus status;

    public AllocationRun(String runId, String policyVersion, String strategyVersion,
                         LocalDateTime executedAt, String executedBy, List<AllocationDecision> decisions,
                         double globalUtilityScore, RunStatus status) {
        if (runId == null || runId.trim().isEmpty()) {
            throw new IllegalArgumentException("Run ID cannot be empty");
        }
        this.runId = runId;
        this.policyVersion = policyVersion != null ? policyVersion : "v1.0";
        this.strategyVersion = strategyVersion != null ? strategyVersion : "GreedyPriority-v1";
        this.executedAt = executedAt != null ? executedAt : LocalDateTime.now();
        this.executedBy = executedBy != null ? executedBy : "SYSTEM";
        this.decisions = decisions != null ? new ArrayList<>(decisions) : new ArrayList<>();
        this.globalUtilityScore = globalUtilityScore;
        this.status = status != null ? status : RunStatus.RUNNING;
    }

    public String getRunId() { return runId; }
    public String getPolicyVersion() { return policyVersion; }
    public String getStrategyVersion() { return strategyVersion; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public String getExecutedBy() { return executedBy; }
    public List<AllocationDecision> getDecisions() { return Collections.unmodifiableList(decisions); }
    public double getGlobalUtilityScore() { return globalUtilityScore; }
    public RunStatus getStatus() { return status; }

    public void addDecision(AllocationDecision decision) {
        if (decision != null) {
            this.decisions.add(decision);
        }
    }

    public void complete(double globalScore) {
        this.globalUtilityScore = globalScore;
        this.status = RunStatus.COMPLETED;
    }

    public void fail() {
        this.status = RunStatus.FAILED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationRun that = (AllocationRun) o;
        return Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId);
    }

    @Override
    public String toString() {
        return "AllocationRun #" + runId + " [Status: " + status + ", GlobalScore: " + globalUtilityScore + "]";
    }
}
