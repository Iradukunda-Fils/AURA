package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import rw.ac.auca.aura.domain.allocation.AllocationRun;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "aura_allocation_runs")
public class AllocationRunEntity {

    @Id
    @Column(name = "run_id", nullable = false)
    private String runId;

    @Column(name = "policy_version", nullable = false, length = 50)
    private String policyVersion;

    @Column(name = "strategy_version", nullable = false, length = 50)
    private String strategyVersion;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "executed_by", nullable = false, length = 100)
    private String executedBy;

    @Column(name = "global_utility_score", nullable = false)
    private double globalUtilityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AllocationRun.RunStatus status;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AllocationDecisionEntity> decisions = new ArrayList<>();

    public AllocationRunEntity() {}

    public AllocationRunEntity(String runId, String policyVersion, String strategyVersion, LocalDateTime executedAt, String executedBy, double globalUtilityScore, AllocationRun.RunStatus status) {
        this.runId = runId;
        this.policyVersion = policyVersion;
        this.strategyVersion = strategyVersion;
        this.executedAt = executedAt;
        this.executedBy = executedBy;
        this.globalUtilityScore = globalUtilityScore;
        this.status = status;
    }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getPolicyVersion() { return policyVersion; }
    public void setPolicyVersion(String policyVersion) { this.policyVersion = policyVersion; }

    public String getStrategyVersion() { return strategyVersion; }
    public void setStrategyVersion(String strategyVersion) { this.strategyVersion = strategyVersion; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }

    public double getGlobalUtilityScore() { return globalUtilityScore; }
    public void setGlobalUtilityScore(double globalUtilityScore) { this.globalUtilityScore = globalUtilityScore; }

    public AllocationRun.RunStatus getStatus() { return status; }
    public void setStatus(AllocationRun.RunStatus status) { this.status = status; }

    public List<AllocationDecisionEntity> getDecisions() { return decisions; }
    public void setDecisions(List<AllocationDecisionEntity> decisions) { this.decisions = decisions; }

    public void addDecision(AllocationDecisionEntity decision) {
        decisions.add(decision);
        decision.setRun(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationRunEntity that = (AllocationRunEntity) o;
        return Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId);
    }
}
