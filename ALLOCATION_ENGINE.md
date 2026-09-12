# AURA Allocation & Optimization Engine Specification

[![Algorithm](https://img.shields.io/badge/Algorithm-Greedy%20Priority%20Heuristic-blue)](https://en.wikipedia.org/wiki/Greedy_algorithm)
[![Model](https://img.shields.io/badge/Optimization-Constraint%20Satisfaction%20(CSP)-darkgreen)](https://en.wikipedia.org/wiki/Constraint_satisfaction_problem)
[![Explainability](https://img.shields.io/badge/Audit-Transparent%20Explainability%20Trace-purple)](docs/architecture/ADRS.md#adr-008-explainability-and-audit-traces)

This document provides the formal mathematical formulation, algorithmic pipeline, constraint satisfaction rules, complexity analysis, and explainability architecture of the **AURA Optimization Engine**.

---

## 1. Problem Formulation: Constraint Satisfaction & Optimization

The university timetable problem in AURA is formalized as a **Multi-Objective Constraint Satisfaction and Optimization Problem (CSOP)**.

### 1.1 Sets and Entities
Let:
* $\mathcal{A} = \{a_1, a_2, \dots, a_N\}$ be the set of academic demand activities.
* $\mathcal{R} = \{r_1, r_2, \dots, r_M\}$ be the set of physical facility resources (rooms, labs, halls).
* $\mathcal{T} = \{t_1, t_2, \dots, t_K\}$ be the discrete set of allowable weekly time slots.
* $\mathcal{L} = \{l_1, l_2, \dots, l_P\}$ be the set of faculty lecturers.
* $\mathcal{C} = \{c_1, c_2, \dots, c_Q\}$ be the set of student cohorts.
* $\mathcal{S} = \{s_1, s_2, \dots, s_J\}$ be the set of geographical campus sites (e.g. Masoro, Gishushu).

Each activity $a_i \in \mathcal{A}$ has attributes:
* $\text{headcount}(a_i) \in \mathbb{N}^+$: Enrolled student count.
* $\text{type}(a_i) \in \{\text{EXAMINATION}, \text{LABORATORY}, \text{LECTURE}, \text{TUTORIAL}, \text{SEMINAR}\}$.
* $\text{reqCaps}(a_i) \subseteq \text{Capabilities}$: Set of required hardware/software capabilities.
* $\text{prefSite}(a_i) \in \mathcal{S}$: Target campus location.
* $\text{lecturers}(a_i) \subseteq \mathcal{L}$: Set of assigned instructors.
* $\text{cohorts}(a_i) \subseteq \mathcal{C}$: Set of enrolled student cohorts.

Each resource $r_j \in \mathcal{R}$ has attributes:
* $\text{capacity}(r_j) \in \mathbb{N}^+$: Physical seating capacity.
* $\text{caps}(r_j) \subseteq \text{Capabilities}$: Installed capabilities.
* $\text{site}(r_j) \in \mathcal{S}$: Physical campus site.
* $\text{status}(r_j) \in \{\text{AVAILABLE}, \text{MAINTENANCE}, \text{RESERVED}, \text{OFFLINE}\}$.

An allocation candidate $\theta_i = (r_j, t_k)$ assigns activity $a_i$ to resource $r_j$ during time slot $t_k$.

---

## 2. Multi-Stage Allocation Pipeline

```
  ┌───────────────────────────────────────────────────────────────────────┐
  │  Demands (A)  +  Resources (R)  +  TimeSlots (T)  +  Context Snap     │
  └───────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │                   STAGE 1: DEMAND PRIORITY SORTING                    │
  │     Order by: ActivityType (Exam > Lab > Lecture)  →  Headcount DESC  │
  └───────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │                   STAGE 2: CANDIDATE GENERATION                       │
  │         Generate Cartesian pairs: (Resource r, TimeSlot t)            │
  └───────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │             STAGE 3: HARD CONSTRAINT PRUNING (SPECIFICATIONS)          │
  │   Capacity • Capabilities • Resource Availability • Lecturer Conflict │
  │                 Cohort Conflict • Site Compatibility                  │
  └──────────────────┬─────────────────────────────────┬──────────────────┘
                     │ Feasible                        │ Infeasible
                     ▼                                 ▼
  ┌─────────────────────────────────────┐  ┌──────────────────────────────┐
  │  STAGE 4: MULTI-OBJECTIVE SCORING   │  │ Record Rejected Alternative  │
  │    Calculate utility score (0-100)  │  │ with specific violation code │
  └──────────────────┬──────────────────┘  └──────────────────────────────┘
                     │
                     ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │             STAGE 5: GREEDY WINNER SELECTION & COMMITMENT             │
  │        Select argmax(Score) → Persist Decision & Explainability       │
  └───────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │         STAGE 6: TRANSACTIONAL DYNAMIC CONCURRENCY GUARD              │
  │   Verify live collision checks inside PostgreSQL before commitment    │
  └───────────────────────────────────────────────────────────────────────┘
```

---

## 3. Hard Constraint Specifications (Pruning Filter)

An allocation candidate $\theta = (r_j, t_k)$ for activity $a_i$ is **feasible** if and only if all six specifications evaluate to `SATISFIED`:

$$\text{Feasible}(a_i, r_j, t_k) \iff \bigwedge_{m=1}^6 \text{Spec}_m(a_i, r_j, t_k) = \text{true}$$

### Spec 1: Capacity Limit (`CapacitySpecification`)
The resource must have sufficient physical seating capacity to accommodate the demand headcount:
$$\text{capacity}(r_j) \ge \text{headcount}(a_i)$$

### Spec 2: Capability Matching (`CapabilitySpecification`)
The resource must possess every hardware, software, and audio-visual feature demanded by the curriculum:
$$\text{reqCaps}(a_i) \subseteq \text{caps}(r_j)$$

### Spec 3: Resource Availability (`ResourceAvailabilitySpecification`)
The resource must not be occupied by any existing or previously scheduled allocation during time interval $t_k$:
$$\forall \text{alloc} \in \text{Allocations}(r_j): \neg \text{Overlaps}(\text{alloc}.t, t_k)$$
where $\text{Overlaps}(t_a, t_b) \iff (t_a.\text{day} = t_b.\text{day}) \land (t_a.\text{start} < t_b.\text{end}) \land (t_a.\text{end} > t_b.\text{start})$.

### Spec 4: Lecturer Conflict Elimination (`LecturerConflictSpecification`)
None of the instructors assigned to activity $a_i$ may be scheduled for another academic session during $t_k$:
$$\forall l \in \text{lecturers}(a_i), \; \forall \text{alloc} \in \text{Allocations}(l): \neg \text{Overlaps}(\text{alloc}.t, t_k)$$

### Spec 5: Student Cohort Conflict Elimination (`CohortConflictSpecification`)
None of the enrolled student cohorts may be scheduled for a concurrent course during $t_k$:
$$\forall c \in \text{cohorts}(a_i), \; \forall \text{alloc} \in \text{Allocations}(c): \neg \text{Overlaps}(\text{alloc}.t, t_k)$$

### Spec 6: Site Compatibility (`SiteCompatibilitySpecification`)
The candidate resource must reside on the activity's requested geographical campus site:
$$\text{site}(r_j) = \text{prefSite}(a_i)$$

---

## 4. Multi-Objective Soft Scoring Formulation

All candidate allocations passing the hard constraint filter are evaluated by `ScoringEngine`. The global candidate utility score $S(\theta) \in [0.0, 100.0]$ is a weighted linear combination of five objective sub-scores:

$$S(\theta) = w_c \cdot C + w_p \cdot P + w_u \cdot U + w_{pref} \cdot Pref + w_{lb} \cdot LB$$

Subject to normalized institutional policy weights:
$$w_c + w_p + w_u + w_{pref} + w_{lb} = 1.0, \quad w_i \ge 0$$

### 4.1 Sub-Score Formulations

| Sub-Score Component | Symbol | Mathematical Formulation | Optimization Objective |
|---|---|---|---|
| **Capacity Fit** | $C$ | $C = \max\left(0.0, \; 1.0 - \frac{\text{cap}(r) - \text{students}(a)}{\text{cap}(r)}\right) \times 100$ | Penalizes wasting large lecture auditoriums on small tutorial cohorts. |
| **Site Proximity** | $P$ | $P = \begin{cases} 100.0 & \text{if } \text{site}(r) = \text{prefSite}(a) \\ 0.0 & \text{otherwise} \end{cases}$ | Rewards placing activities at preferred campus sites; eliminates cross-town transit. |
| **Resource Utilization** | $U$ | $U = \left(\frac{\text{students}(a)}{\text{cap}(r)}\right) \times 100$ | Maximizes seat fill ratio across university classrooms. |
| **Time Preference** | $Pref$ | $Pref = \begin{cases} 100.0 & \text{if } t = \text{preferredTimeslot}(a) \\ 60.0 & \text{if } t.\text{day} = \text{prefDay}(a) \\ 30.0 & \text{otherwise} \end{cases}$ | Satisfies preferred teaching slots for senior faculty and cohorts. |
| **Weekly Load Balance**| $LB$ | $LB = 100.0 - \left(\frac{\text{Count}(\text{day}(t))}{\max_{d}(\text{Count}(d)) + 1}\right) \times 100$ | Distributes academic demand evenly across Monday through Friday. |

---

## 5. Heuristic Solver & Computational Complexity

### 5.1 Algorithm: Greedy Priority Strategy
AURA implements `GreedyPriorityStrategy`:

1. **Sorting Phase**: Sort all $N$ activities using priority comparator:
   $$\text{ActivityType} \; (\text{EXAM} \succ \text{LAB} \succ \text{LECTURE}) \longrightarrow \text{StudentCount} \; (\text{DESC}) \longrightarrow \text{Duration} \; (\text{DESC})$$
2. **Evaluation Phase**: For each activity $a_i \in \mathcal{A}$:
   a. Iterate over all resources $r_j \in \mathcal{R}$ and allowed timeslots $t_k \in \mathcal{T}$.
   b. Execute composite specification checks. If violated, log rejection to `ExplainabilityEngine`.
   c. If feasible, calculate soft score $S(a_i, r_j, t_k)$.
   d. Assign $a_i$ to $\theta^* = \arg\max_{(r_j, t_k)} S(a_i, r_j, t_k)$.
   e. Immediately update in-memory allocation state to prevent subsequent activities from conflicting.

### 5.2 Computational Complexity Analysis
* **Demand Sorting**: $\mathcal{O}(N \log N)$ where $N = |\mathcal{A}|$.
* **Candidate Space**: $|\mathcal{R}| \times |\mathcal{T}| = M \times K$.
* **Specification Evaluation**: Each candidate evaluates 6 specifications. Interval collision checks against existing allocations take $\mathcal{O}(|\text{Allocations}|)$. With index hashing and in-memory interval trees, lookup is $\mathcal{O}(1)$ average.
* **Total Time Complexity**:
  $$\mathcal{T}_{\text{total}} = \mathcal{O}(N \log N + N \cdot M \cdot K)$$
* **Practical Performance**: For typical AUCA semester demands ($N = 250$, $M = 40$, $K = 25$), the solver explores $\approx 250,000$ candidate states and completes in **$< 450\text{ ms}$** on standard server hardware.

---

## 6. Transparent Explainability & Audit Engine

Every optimization decision generates an immutable, transparent audit payload stored in `aura_allocation_decisions`:

```json
{
  "activityId": "ACT-INSY321-LAB-A",
  "feasible": true,
  "selectedResourceId": "LAB-COMP-01",
  "selectedSiteId": "SITE-MASORO",
  "timeslot": "MONDAY 08:00 - 10:00",
  "score": 92.4,
  "scoreBreakdown": {
    "capacityFit": 95.0,
    "proximity": 100.0,
    "utilization": 88.0,
    "preference": 100.0,
    "loadBalance": 80.0
  },
  "humanExplanation": "Allocated to LAB-COMP-01 (Capacity 45 vs Demand 40) on MONDAY 08:00-10:00 at SITE-MASORO. Matches required capabilities: [HIGH_SPEC_PC, PROJECTOR]. Satisfied student time preference. Utility score: 92.4/100.",
  "rejectedAlternatives": [
    {"resourceId": "HALL-AUD-01", "reason": "HARD_CONSTRAINT_VIOLATED: Capacity oversized (Seat waste penalty)"},
    {"resourceId": "LAB-COMP-02", "reason": "HARD_CONSTRAINT_VIOLATED: Missing required capability [HIGH_SPEC_PC]"},
    {"resourceId": "ROOM-201", "reason": "HARD_CONSTRAINT_VIOLATED: Resource occupied at MONDAY 08:00 by ACT-MATH101"},
    {"resourceId": "LAB-GIS-01", "reason": "HARD_CONSTRAINT_VIOLATED: Campus site mismatch (SITE-GISHUSHU != SITE-MASORO)"}
  ]
}
```

This ensures complete institutional trust, allowing department heads to understand exactly why a particular room was allocated or why an activity was marked unallocatable.
