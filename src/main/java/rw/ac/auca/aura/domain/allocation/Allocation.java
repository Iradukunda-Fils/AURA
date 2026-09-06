package rw.ac.auca.aura.domain.allocation;

import rw.ac.auca.aura.domain.scheduling.TimeSlot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain Aggregate representing an operational committed binding of an AcademicActivity to resources, time, and site.
 * Committed allocations are append-only/immutable.
 */
public class Allocation implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum AllocationStatus { COMMITTED, CANCELLED, SUPERSEDED }

    private final String id;
    private final String runId;
    private final String activityId;
    private final Set<String> resourceIds;
    private final TimeSlot timeSlot;
    private final String siteId;
    private final LocalDateTime committedAt;
    private final String committedBy;
    private AllocationStatus status;

    public Allocation(String id, String runId, String activityId, Set<String> resourceIds,
                      TimeSlot timeSlot, String siteId, LocalDateTime committedAt, String committedBy, AllocationStatus status) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Allocation ID cannot be empty");
        }
        if (activityId == null || activityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity ID cannot be empty");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("TimeSlot cannot be null");
        }
        this.id = id;
        this.runId = runId != null ? runId : "";
        this.activityId = activityId;
        this.resourceIds = resourceIds != null ? Collections.unmodifiableSet(new HashSet<>(resourceIds)) : Collections.emptySet();
        this.timeSlot = timeSlot;
        this.siteId = siteId != null ? siteId : "";
        this.committedAt = committedAt != null ? committedAt : LocalDateTime.now();
        this.committedBy = committedBy != null ? committedBy : "SYSTEM";
        this.status = status != null ? status : AllocationStatus.COMMITTED;
    }

    public String getId() { return id; }
    public String getRunId() { return runId; }
    public String getActivityId() { return activityId; }
    public Set<String> getResourceIds() { return resourceIds; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public String getSiteId() { return siteId; }
    public LocalDateTime getCommittedAt() { return committedAt; }
    public String getCommittedBy() { return committedBy; }
    public AllocationStatus getStatus() { return status; }

    public boolean isCommitted() {
        return status == AllocationStatus.COMMITTED;
    }

    public void cancel() {
        this.status = AllocationStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allocation that = (Allocation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Allocation #" + id + " [Activity: " + activityId + ", Resources: " + resourceIds + ", Time: " + timeSlot + "]";
    }
}
