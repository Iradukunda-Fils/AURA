# AURA System Architecture & Engineering Specification

[![Architecture](https://img.shields.io/badge/Architecture-Modular--Monolith-blue)](https://martinfowler.com/bliki/MonolithFirst.html)
[![Pattern](https://img.shields.io/badge/Pattern-Clean%20%2F%20Hexagonal-brightgreen)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
[![Language](https://img.shields.io/badge/Language-Java%2021%20LTS-orange)](https://jdk.java.net/21/)
[![Framework](https://img.shields.io/badge/Framework-Jakarta%20EE%2010-purple)](https://jakarta.ee/)

This document defines the architectural blueprints, tactical and strategic Domain-Driven Design (DDD) patterns, component topologies, state machine lifecycles, and concurrency control invariants for the **AURA (AUCA Resource Allocation & Optimization System)** platform.

---

## 1. Institutional Context & System Mission

### 1.1 The Institutional Scheduling Challenge
Universities routinely struggle with resource contention: lecture halls are over-booked while specialized computer laboratories sit idle; student cohorts are assigned overlapping classes; instructors are scheduled across different campuses with zero transit time.

Traditional room booking software operates under a simple, reactive question:
> **"Is Room 101 available at 08:00 on Monday?"**

AURA solves the problem globally by reframing the question as a multi-objective optimization problem:
> **"Given all competing academic demands, faculty availability, student cohort progression, required physical capabilities, multi-campus transit constraints, and university policy weights—what is the mathematically optimal, conflict-free institutional timetable?"**

---

## 2. C4 Architecture Models

### 2.1 Level 1: System Context Diagram
The System Context diagram illustrates how AUCA administrators, faculty, and students interact with AURA, and where AURA sits in relation to external university infrastructure.

```mermaid
C4Context
    title System Context Diagram for AUCA Resource Allocation System (AURA)

    Person(admin, "Academic Administrator", "Configures policy scoring weights, triggers optimization solver, inspects explainability traces, and commits schedules.")
    Person(student, "AUCA Student / Faculty", "Explores published timetables, inspects room allocations, and reviews weekly academic schedules.")

    System(aura, "AURA Platform", "Autonomous institutional optimization engine, conflict validator, and timetable publishing platform.")

    System_Ext(sis, "AUCA Student Information System (Future)", "External repository of enrolled students, course catalogs, and academic records.")
    System_Ext(ldap, "AUCA Identity & Access Management (Future)", "Centralized LDAP/OAuth2 authentication provider.")

    Rel(admin, aura, "Runs optimization, configures policies, commits timetables", "HTTPS / Web Console")
    Rel(student, aura, "Views weekly timetables, filters rooms and cohorts", "HTTPS / Web Explorer")
    Rel(aura, sis, "Synchronizes student rosters and courses (Phase 3)", "REST API")
    Rel(aura, ldap, "Authenticates academic principals (Phase 3)", "LDAPS")
```

### 2.2 Level 2: Container Diagram
AURA is containerized as a lean, highly resilient service stack orchestrated via Docker Compose:

```mermaid
C4Container
    title Container Diagram for AURA Platform

    Person(user, "University Stakeholder", "Administrator, Faculty, or Student")

    Container_Boundary(c1, "AURA Application Infrastructure") {
        Container(web, "Web Presentation & UI", "Jakarta Faces (JSF 4.0), CDI, HTML5, Vanilla CSS", "Delivers responsive dark-mode consoles, schedule explorers, and run histories.")
        Container(app, "Application & Optimization Core", "Java 21 LTS, Greedy Heuristic Solver, Specification Engine", "Executes multi-objective constraint satisfaction, scoring, and concurrency validation.")
        Container(repo, "Persistence & Repositories", "Hibernate ORM 7.0, JPA 3.2, GenericDao", "Translates pure domain models to relational entities and executes ACID transactions.")
        ContainerDb(db, "Relational Database", "PostgreSQL 17", "Stores institutional physical assets, academic curricula, allocation runs, decision audits, and committed schedules.")
    }

    Rel(user, web, "Interacts via browser", "HTTPS / Port 8080")
    Rel(web, app, "Delegates UI actions", "In-Process Java Method Calls")
    Rel(app, repo, "Queries context & persists decisions", "Domain Repository Interfaces")
    Rel(repo, db, "Executes queries & transactions", "JDBC / RESOURCE_LOCAL (Port 5432)")
```

---

## 3. Clean / Hexagonal Architectural Layers

AURA follows a strict **Onion / Clean Architecture** pattern. Outer infrastructure layers depend strictly inward upon inner business layers. The Domain Core has **zero dependencies** on frameworks, web libraries, or JPA annotations.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER (JSF 4.0)                       │
│       AdminAllocationBean, StudentIntentBean, Custom UI Components       │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ calls
┌────────────────────────────────────▼────────────────────────────────────┐
│                       APPLICATION SERVICE LAYER                         │
│           AllocationApplicationService, AcademicContextEngine            │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ orchestrates
┌────────────────────────────────────▼────────────────────────────────────┐
│                          DOMAIN CORE LAYER                              │
│   Entities: AcademicActivity, Resource, TimeSlot, AllocationRun         │
│   Specifications: Capacity, Capability, Availability, ConflictSpecs     │
│   Scoring: ScoringEngine, GreedyPriorityStrategy, ExplainabilityEngine  │
└────────────────────────────────────▲────────────────────────────────────┘
                                     │ implements
┌────────────────────────────────────┴────────────────────────────────────┐
│                    REPOSITORY & INFRASTRUCTURE LAYER                    │
│   Interfaces: ResourceRepository, AllocationRepository, RunRepository  │
│   Impls: GenericDao, Hibernate*Repository, JPA Entities, Mappers        │
│   Persistence: JpaUtil, PostgreSQL 17, DatabaseInitializer              │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Package Organization Map

| Package Path | Architectural Layer | Responsibilities & Design Rules |
|---|---|---|
| `rw.ac.auca.aura.domain.shared` | Domain Shared Kernel | Fundamental value objects (`Capability`, `CapabilitySet`, `TimeSlot`, `ScoreBreakdown`). |
| `rw.ac.auca.aura.domain.resource` | Domain Spatial Subdomain | Physical models (`Site`, `Building`, `Resource`, `ResourceType`, `ResourceStatus`). |
| `rw.ac.auca.aura.domain.academic` | Domain Academic Subdomain | Curriculum & demand entities (`AcademicActivity`, `Course`, `CourseOffering`, `Department`, `Lecturer`, `StudentCohort`). |
| `rw.ac.auca.aura.domain.constraint` | Domain Specification Rules | Hard constraint rules implementing `ResourceSpecification` (`CapacitySpecification`, `CapabilitySpecification`, `ResourceAvailabilitySpecification`, `LecturerConflictSpecification`, `CohortConflictSpecification`, `SiteCompatibilitySpecification`). |
| `rw.ac.auca.aura.domain.allocation` | Domain Optimization Solver | Solvers and audit engines (`GreedyPriorityStrategy`, `ScoringEngine`, `ExplainabilityEngine`, `AllocationDecision`). |
| `rw.ac.auca.aura.domain.repository` | Domain Repository SPI | Dependency Inversion interfaces (`ResourceRepository`, `AllocationRepository`, `AllocationRunRepository`, etc.). |
| `rw.ac.auca.aura.application.*` | Application Services | Orchestrates transactions, snapshot generation, dynamic revalidation, and use cases. |
| `rw.ac.auca.aura.infrastructure.persistence.entities` | Infrastructure Persistence | JPA Entity mappings (`*Entity.java`) targeting PostgreSQL `aura_*` tables. |
| `rw.ac.auca.aura.infrastructure.persistence.mappers` | Infrastructure Mappers | Pure Domain $\longleftrightarrow$ JPA Entity bi-directional conversion. |
| `rw.ac.auca.aura.infrastructure.persistence.repositories` | Infrastructure Persistence | Hibernate JPA implementations extending `GenericDao<T, ID>`. |
| `rw.ac.auca.aura.presentation` | Presentation (JSF) | Backing beans (`AdminAllocationBean`, `StudentIntentBean`) and Facelet views. |

---

## 4. Entity Lifecycles & State Machines

### 4.1 AcademicActivity Lifecycle
Every demand unit advances through a deterministic four-state lifecycle:

```mermaid
stateDiagram-v2
    [*] --> UNSCHEDULED: Course offering creates activity demand

    UNSCHEDULED --> SCHEDULED: Optimization run pairs activity with feasible candidate
    UNSCHEDULED --> UNSCHEDULED: Optimization run finds no feasible candidate (Infeasible)

    SCHEDULED --> COMMITTED: Administrator approves and commits allocation run
    SCHEDULED --> UNSCHEDULED: Administrator discards or re-runs optimization

    COMMITTED --> CANCELLED: Administrative schedule revision / cancellation
    CANCELLED --> [*]
```

### 4.2 AllocationRun Lifecycle
Optimization runs capture engine executions:

```mermaid
stateDiagram-v2
    [*] --> EXECUTING: Admin triggers allocation run

    EXECUTING --> COMPLETED: All demands successfully allocated (Feasible = 100%)
    EXECUTING --> FEASIBLE_PARTIAL: Some demands unallocated due to hard constraint bottlenecks
    EXECUTING --> INFEASIBLE: Zero demands could satisfy hard constraints
    EXECUTING --> FAILED: Unexpected exception during solver execution

    COMPLETED --> [*]
    FEASIBLE_PARTIAL --> [*]
    INFEASIBLE --> [*]
    FAILED --> [*]
```

### 4.3 Allocation Entity Lifecycle
Committed schedules published for institutional display:

```mermaid
stateDiagram-v2
    [*] --> PROPOSED: Generated during solver evaluation

    PROPOSED --> COMMITTED: Passed dynamic concurrency revalidation & committed
    PROPOSED --> REJECTED: Concurrency conflict detected or discarded by admin

    COMMITTED --> CANCELLED: Operational schedule revoked
    REJECTED --> [*]
    CANCELLED --> [*]
```

---

## 5. End-to-End Optimization Execution Sequence

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Academic Dean / Admin
    participant Bean as AdminAllocationBean
    participant AppService as AllocationApplicationService
    participant ContextEngine as ContextEngine
    participant Solver as GreedyPriorityStrategy
    participant Spec as CompositeAndSpecification
    participant Scorer as ScoringEngine
    participant Auditor as ExplainabilityEngine
    participant Repo as HibernateAllocationRunRepository
    participant DB as PostgreSQL 17

    Admin->>Bean: Click "Run Institutional Optimization"
    Bean->>AppService: executeAllocationRun(policyId, adminUsername)
    
    AppService->>ContextEngine: buildContext()
    ContextEngine->>DB: Query active resources, active demands, committed schedules
    DB-->>ContextEngine: Master snapshot data
    ContextEngine-->>AppService: SpecificationContext snapshot

    AppService->>Solver: allocate(demands, resources, policy, context)
    
    loop For each AcademicActivity (ordered by priority: Exam > Lab > Lecture)
        loop For each candidate Resource & TimeSlot
            Solver->>Spec: isSatisfiedBy(resource, activity, slot, context)
            Note over Spec: Evaluates Capacity, Capabilities, Room Availability, Lecturer & Cohort Conflicts
            alt Hard Constraint Violated
                Spec-->>Solver: ConstraintResult(VIOLATED, reason)
                Solver->>Auditor: recordRejectedAlternative(candidate, reason)
            else All Hard Constraints Passed
                Spec-->>Solver: ConstraintResult(SATISFIED)
                Solver->>Scorer: calculateUtility(resource, activity, slot, policy)
                Scorer-->>Solver: Utility Score (0 - 100) & SubScoreBreakdown
            end
        end
        Solver->>Solver: Select Candidate with Maximum Utility Score
        Solver->>Auditor: createDecisionTrace(winningCandidate, alternatives)
    end

    Solver-->>AppService: AllocationRun (Decisions, Global Utility Score)
    AppService->>Repo: save(AllocationRunEntity)
    Repo->>DB: INSERT into aura_allocation_runs & aura_allocation_decisions
    AppService-->>Bean: Return AllocationRun Result
    Bean-->>Admin: Render Proposed Allocation Table, KPI Metrics & Explainability Logs
```

---

## 6. Concurrency Revalidation Invariant

A critical architectural requirement is that **no double-booking may ever enter the database**, even under concurrent administrator sessions:

```
T0: Admin A starts Optimization Run (Snapshot: 10 committed allocations)
T1: Admin B starts Optimization Run (Snapshot: 10 committed allocations)
T2: Admin A commits Run A (Creates Allocation 11 for Room 101 on Mon 08:00) -> SUCCESS
T3: Admin B attempts to commit Run B (Proposes Room 101 on Mon 08:00)
    │
    ▼
    [ Transactional Concurrency Revalidation in commitRun() ]
    1. Query fresh committed allocations from PostgreSQL inside active transaction.
    2. Check: Does Room 101 have an overlapping committed allocation on Mon 08:00?
    3. Collision DETECTED (Allocation 11 from Admin A).
    4. Transaction is immediately ABORTED / ROLLED BACK.
    5. Admin B receives explicit error: "Concurrency conflict: Room 101 is already committed at target timeslot."
```

---

## 7. Extensibility & Future Architecture (Phase 3 Roadmap)

AURA's modular monolith architecture provides clean extension points without refactoring existing domain code:

1. **Alternative Optimization Solvers**: Implement `AllocationStrategy` to swap the greedy heuristic for a Genetic Algorithm (GA), Integer Linear Programming (ILP via OptaPlanner/Timefold), or Simulated Annealing.
2. **New Hard Constraints**: Implement `ResourceSpecification` (e.g., `MaxConsecutiveLecturesSpecification`, `WheelchairAccessibilitySpecification`) and inject into `CompositeAndSpecification`.
3. **Role-Based Access Control (RBAC)**: Enforce security filters on CDI backing beans (`@RolesAllowed("ADMIN")`, `@RolesAllowed("STUDENT")`).
4. **SIS Real-Time Integration**: Implement messaging adapters (Kafka / RabbitMQ) under `infrastructure/messaging` to synchronize enrollments automatically.
