package rw.ac.auca.aura.presentation;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import rw.ac.auca.aura.application.allocation.AllocationApplicationService;
import rw.ac.auca.aura.domain.allocation.Allocation;
import rw.ac.auca.aura.domain.allocation.AllocationDecision;
import rw.ac.auca.aura.domain.allocation.AllocationRun;
import rw.ac.auca.aura.domain.policy.ScoringWeightPolicy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named("adminBean")
@SessionScoped
public class AdminAllocationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private AllocationApplicationService allocationService;
    private String currentRunId;
    private AllocationRun activeRun;
    private AllocationDecision selectedDecision;

    // Weight Policy inputs
    private double weightContinuity = 25.0;
    private double weightProximity = 20.0;
    private double weightUtilization = 25.0;
    private double weightPreference = 15.0;
    private double weightLoadBalance = 15.0;

    private String statusMessage;

    @PostConstruct
    public void init() {
        this.allocationService = new AllocationApplicationService();
        runOptimizationSimulation();
    }

    public void runOptimizationSimulation() {
        ScoringWeightPolicy customPolicy = new ScoringWeightPolicy(
                weightContinuity,
                weightProximity,
                weightUtilization,
                weightPreference,
                weightLoadBalance
        );
        allocationService.updateWeightPolicy(customPolicy);

        String runId = "RUN_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        this.activeRun = allocationService.executeSimulationRun(runId, "AdminConsoleUser");
        this.currentRunId = runId;

        if (activeRun != null && !activeRun.getDecisions().isEmpty()) {
            this.selectedDecision = activeRun.getDecisions().get(0);
        }

        long feasibleCount = activeRun != null ? activeRun.getDecisions().stream().filter(AllocationDecision::isFeasible).count() : 0;

        this.statusMessage = "Simulation " + runId + " completed: " + 
                feasibleCount + " feasible allocations proposed.";
    }

    public void commitRun() {
        if (activeRun == null) {
            statusMessage = "No active simulation run to commit.";
            return;
        }

        boolean success = allocationService.commitRun(activeRun.getRunId(), "AdminUser");
        if (success) {
            statusMessage = "Allocation Run " + activeRun.getRunId() + " successfully approved and committed to live schedule!";
        } else {
            statusMessage = "Failed to commit run " + activeRun.getRunId();
        }
    }

    public void resetDemoData() {
        allocationService.seedSampleAucaData();
        runOptimizationSimulation();
        statusMessage = "System re-seeded with clean AUCA campus data and simulation executed.";
    }

    public List<AllocationRun> getRunHistory() {
        return allocationService.getAllRuns();
    }

    public List<Allocation> getCommittedAllocations() {
        return allocationService.getActiveAllocations();
    }

    public AllocationApplicationService getAllocationService() {
        return allocationService;
    }

    public AllocationRun getActiveRun() {
        return activeRun;
    }

    public String getCurrentRunId() {
        return currentRunId;
    }

    public AllocationDecision getSelectedDecision() {
        return selectedDecision;
    }

    public void setSelectedDecision(AllocationDecision selectedDecision) {
        this.selectedDecision = selectedDecision;
    }

    public double getWeightContinuity() { return weightContinuity; }
    public void setWeightContinuity(double weightContinuity) { this.weightContinuity = weightContinuity; }

    public double getWeightProximity() { return weightProximity; }
    public void setWeightProximity(double weightProximity) { this.weightProximity = weightProximity; }

    public double getWeightUtilization() { return weightUtilization; }
    public void setWeightUtilization(double weightUtilization) { this.weightUtilization = weightUtilization; }

    public double getWeightPreference() { return weightPreference; }
    public void setWeightPreference(double weightPreference) { this.weightPreference = weightPreference; }

    public double getWeightLoadBalance() { return weightLoadBalance; }
    public void setWeightLoadBalance(double weightLoadBalance) { this.weightLoadBalance = weightLoadBalance; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
