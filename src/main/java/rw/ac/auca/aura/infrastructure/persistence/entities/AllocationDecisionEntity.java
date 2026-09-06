package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "aura_allocation_decisions")
public class AllocationDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false, foreignKey = @ForeignKey(name = "fk_decision_run"))
    private AllocationRunEntity run;

    @Column(name = "activity_id", nullable = false, length = 100)
    private String activityId;

    @Column(name = "selected_resource_id", length = 100)
    private String selectedResourceId;

    @Column(name = "selected_site_id", length = 50)
    private String selectedSiteId;

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean feasible;

    @Column(nullable = false)
    private double score;

    @Column(name = "human_explanation", columnDefinition = "TEXT")
    private String humanExplanation;

    @Column(name = "score_breakdown_json", length = 2000)
    private String scoreBreakdownJson;

    @Column(name = "constraint_results_json", length = 4000)
    private String constraintResultsJson;

    @Column(name = "rejected_alternatives_json", columnDefinition = "TEXT")
    private String rejectedAlternativesJson;

    public AllocationDecisionEntity() {}

    public AllocationDecisionEntity(AllocationRunEntity run, String activityId, String selectedResourceId, String selectedSiteId, String dayOfWeek, LocalTime startTime, LocalTime endTime, boolean feasible, double score, String humanExplanation, String scoreBreakdownJson, String constraintResultsJson, String rejectedAlternativesJson) {
        this.run = run;
        this.activityId = activityId;
        this.selectedResourceId = selectedResourceId;
        this.selectedSiteId = selectedSiteId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.feasible = feasible;
        this.score = score;
        this.humanExplanation = humanExplanation;
        this.scoreBreakdownJson = scoreBreakdownJson;
        this.constraintResultsJson = constraintResultsJson;
        this.rejectedAlternativesJson = rejectedAlternativesJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AllocationRunEntity getRun() { return run; }
    public void setRun(AllocationRunEntity run) { this.run = run; }

    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getSelectedResourceId() { return selectedResourceId; }
    public void setSelectedResourceId(String selectedResourceId) { this.selectedResourceId = selectedResourceId; }

    public String getSelectedSiteId() { return selectedSiteId; }
    public void setSelectedSiteId(String selectedSiteId) { this.selectedSiteId = selectedSiteId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isFeasible() { return feasible; }
    public void setFeasible(boolean feasible) { this.feasible = feasible; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getHumanExplanation() { return humanExplanation; }
    public void setHumanExplanation(String humanExplanation) { this.humanExplanation = humanExplanation; }

    public String getScoreBreakdownJson() { return scoreBreakdownJson; }
    public void setScoreBreakdownJson(String scoreBreakdownJson) { this.scoreBreakdownJson = scoreBreakdownJson; }

    public String getConstraintResultsJson() { return constraintResultsJson; }
    public void setConstraintResultsJson(String constraintResultsJson) { this.constraintResultsJson = constraintResultsJson; }

    public String getRejectedAlternativesJson() { return rejectedAlternativesJson; }
    public void setRejectedAlternativesJson(String rejectedAlternativesJson) { this.rejectedAlternativesJson = rejectedAlternativesJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationDecisionEntity that = (AllocationDecisionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
