# AURA Allocation & Optimization Engine Specification

## 1. Core Allocation Pipeline
The allocation engine executes a multi-stage pipeline:

```
Demand Activities + Spatial Resources + TimeSlots + Context
                          │
                          ▼
             [ Hard Constraint Filter ]
 (CapacitySpecification, CapabilitySpecification, Availability, Conflicts)
                          │
                          ▼
             [ Soft Constraint Scoring ]
 (Continuity, Proximity, Utilization, Preference, Load Balance)
                          │
                          ▼
            [ Heuristic Strategy Solver ]
            (GreedyPriorityStrategy)
                          │
                          ▼
          [ AllocationDecision & Audit Log ]
          (ExplainabilityEngine trace)
                          │
                          ▼
      [ Concurrency Revalidation & Commitment ]
```

---

## 2. Specifications & Constraint Rules
* **CapacitySpecification**: `TargetResource.capacity >= Activity.studentCount`
* **CapabilitySpecification**: `TargetResource.capabilities` contains all `Activity.requiredCapabilities`
* **ResourceAvailabilitySpecification**: Resource is not double-booked at the target timeslot by any existing allocation
* **LecturerConflictSpecification**: Assigned lecturers have no overlapping activity at the target timeslot
* **CohortConflictSpecification**: Assigned student cohorts have no overlapping activity at the target timeslot
* **SiteCompatibilitySpecification**: Target resource is located at the activity's preferred site

---

## 3. Multi-Objective Soft Scoring Formula
Each candidate allocation is assigned a utility score (0.0 to 100.0) calculated as:

$$\text{Score} = w_c \cdot C + w_p \cdot P + w_u \cdot U + w_{pref} \cdot Pref + w_{lb} \cdot LB$$

where:
* $C$: Capacity Fit Score (penalizes oversized rooms for small cohorts)
* $P$: Proximity / Site Score
* $U$: Resource Utilization Efficiency
* $Pref$: Time & Room Preference Match
* $LB$: Faculty / Room Load Balance Score
* $w_c, w_p, w_u, w_{pref}, w_{lb}$: Multi-objective policy weights configured in `AllocationPolicy`

---

## 4. Explainability & Audit Traces
Every decision records:
1. **Feasibility**: Boolean status indicating whether a valid allocation was found.
2. **Score Breakdown**: Individual sub-scores ($C, P, U, Pref, LB$).
3. **Human Explanation**: Plain text narrative explaining why the winning resource was selected.
4. **Rejected Alternatives**: Detailed list of rejected candidate rooms and the hard constraint or lower score that caused rejection.
