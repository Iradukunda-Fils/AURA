# AURA Phase 2 — Architectural Decision Records

These decisions are binding for all Phase 2 implementation work.

---

## ADR-001: Resource → Building → Site Hierarchy (NORMALIZED)

**Decision**: Option A — fully normalized. No redundant `site_id` on `Resource`.

```
Resource → Building → Site
```

`Building.site` is the **only** authoritative site relationship.
To derive a resource's site: `resource.getBuilding().getSite()`.

**Rationale**: AURA is not at scale where denormalization of `site_id` is justified. Eliminating
redundancy prevents the inconsistency where `Resource.siteId ≠ Building.siteId`. If profiling
later proves a read-performance problem, denormalization can be added with a CHECK constraint
or materialized view.

**Impact on domain**: The domain `Resource.siteId` field remains unchanged. The mapper derives
`siteId` from the `Building → Site` chain when constructing the domain object from the entity.

---

## ADR-002: CourseOffering vs AcademicActivity Participant Semantics

**Decision**: Both `CourseOffering` and `AcademicActivity` carry their own lecturer/cohort sets.
They are NOT redundant — they represent different scopes:

- **CourseOffering participants**: Lecturers/cohorts belonging to the entire offering across all
  its activities and the full semester.
- **AcademicActivity participants**: Lecturers/cohorts actually involved in **this specific
  schedulable activity** (e.g. a single lab session may involve only one cohort sub-group).

An `AcademicActivity`'s participant sets are always a **subset of or equal to** its parent
`CourseOffering`'s participant sets. The allocation engine uses `AcademicActivity` participants
for constraint checking (lecturer conflict, cohort conflict), not the offering-level sets.

**Persistence**: Both `CourseOffering` and `AcademicActivity` have independent join tables for
lecturers and cohorts.

---

## ADR-003: Multi-Resource Allocation — Persistence vs Strategy

**Current state**:
- **Domain contract**: `Allocation.resourceIds` is `Set<String>` — supports multiple resources.
- **Persistence design**: Normalized `aura_allocation_resources` join table — supports N resources per allocation.
- **Current GreedyPriorityStrategy**: Produces `Set.of(decision.getSelectedResourceId())` — **single resource per activity** for MVP.

**Rule**: The persistence layer must NOT constrain the domain to a single resource. The join table
exists to support future multi-resource strategies (e.g. a large exam needing two adjacent rooms).

**Labeling**: All documentation and code comments must clearly distinguish:
- Persistence capability: multi-resource
- Current allocation strategy: single-resource (MVP)

---

## ADR-004: JSON Usage Boundary

**Rule**: JSON may be used for **analytical snapshots and semantically flexible data**.
Core relational relationships and query-critical/enforceable data must remain **normalized**.

| Field | Storage | Justification |
|---|---|---|
| `score_breakdown_json` | ✅ JSON | Analytical snapshot — never queried by individual field |
| `constraint_results_json` | ✅ JSON | Diagnostic evidence — read-only after creation |
| `rejected_alternatives_json` | ✅ JSON | Audit trail — never joined or filtered |
| `policy_weights_json` | ✅ JSON | Forward-compatible extensibility snapshot |
| `capabilities_json` | ⚠️ JSON (MVP) | **Trade-off**: Capabilities participate in hard constraints, but the constraint engine operates on domain objects (not SQL). Querying capabilities by SQL is a future concern. Acceptable for MVP. Revisit if "find all resources with PROJECTOR" becomes a SQL-level requirement. |

---

## ADR-005: Concurrency Revalidation on Commit

**Requirement**: `commitRun()` must perform a **final hard-constraint and resource availability
revalidation** inside the commit transaction before persisting allocations.

**Flow**:
```
commitRun(runId)
    ↓
BEGIN TRANSACTION
    ↓
Load proposed decisions from run
    ↓
For each feasible decision:
    Load current committed allocations from DB
    Re-evaluate: is resource still available at timeslot?
    Re-evaluate: lecturer/cohort conflict against current DB state?
    ↓
    If revalidation passes → persist allocation + update activity state
    If revalidation fails → mark decision as STALE, skip, log reason
    ↓
COMMIT TRANSACTION
```

**Rationale**: Between simulation and commitment, another run may have been committed. Without
revalidation, two conflicting allocations can be persisted. This is AURA's most critical
data integrity requirement.

**Implementation phase**: Phase 2I (Transactional Application Services).
