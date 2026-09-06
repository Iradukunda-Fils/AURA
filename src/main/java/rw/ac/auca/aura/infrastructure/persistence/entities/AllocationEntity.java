package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import rw.ac.auca.aura.domain.allocation.Allocation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "aura_allocations")
public class AllocationEntity {

    @Id
    private String id;

    @Column(name = "run_id", nullable = false, length = 100)
    private String runId;

    @Column(name = "activity_id", nullable = false, length = 100)
    private String activityId;

    @Column(name = "site_id", nullable = false, length = 50)
    private String siteId;

    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "committed_at", nullable = false)
    private LocalDateTime committedAt;

    @Column(name = "committed_by", nullable = false, length = 100)
    private String committedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Allocation.AllocationStatus status;

    @OneToMany(mappedBy = "allocation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AllocationResourceEntity> resources = new HashSet<>();

    public AllocationEntity() {}

    public AllocationEntity(String id, String runId, String activityId, String siteId, String dayOfWeek, LocalTime startTime, LocalTime endTime, LocalDateTime committedAt, String committedBy, Allocation.AllocationStatus status) {
        this.id = id;
        this.runId = runId;
        this.activityId = activityId;
        this.siteId = siteId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.committedAt = committedAt;
        this.committedBy = committedBy;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public LocalDateTime getCommittedAt() { return committedAt; }
    public void setCommittedAt(LocalDateTime committedAt) { this.committedAt = committedAt; }

    public String getCommittedBy() { return committedBy; }
    public void setCommittedBy(String committedBy) { this.committedBy = committedBy; }

    public Allocation.AllocationStatus getStatus() { return status; }
    public void setStatus(Allocation.AllocationStatus status) { this.status = status; }

    public Set<AllocationResourceEntity> getResources() { return resources; }
    public void setResources(Set<AllocationResourceEntity> resources) { this.resources = resources; }

    public void addResource(String resourceId) {
        AllocationResourceEntity resourceEntity = new AllocationResourceEntity(this, resourceId);
        resources.add(resourceEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationEntity that = (AllocationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
