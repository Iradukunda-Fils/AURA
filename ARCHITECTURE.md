# AURA Architecture Specification

## 1. System Overview
**AURA (AUCA Resource Allocation & Optimization System)** is an institutional decision-support and optimization platform built for the Adventist University of Central Africa (AUCA).

Unlike standard booking applications that evaluate availability reactively, AURA evaluates competing academic demands, hard constraints, pedagogical requirements, and multi-objective soft preferences to synthesize optimal, conflict-free institutional timetables.

---

## 2. Architectural Drivers & Principles
1. **Modular Monolith**: Clean layer separation (`Presentation → Application → Domain → Repository → Infrastructure → PostgreSQL`).
2. **Domain Purity**: Domain objects are completely free of `@Entity` or infrastructure dependencies.
3. **Deterministic Optimization**: Hard constraints (no double-booking, capacity thresholds, capability matching) are strictly enforced by specification rules before soft-scoring.
4. **Auditability & Explainability**: Every decision trace captures human-readable explanations, rejected alternative reasons, and multi-objective score breakdowns.
5. **Transactional Concurrency Protection**: Live schedule commitment revalidates all hard constraints dynamically to reject stale proposals.

---

## 3. Layered Component Architecture

```
[ JSF Presentation Layer ]
  (AdminAllocationBean, StudentIntentBean, UI Views)
            │
            ▼
[ Application Layer ]
  (AllocationApplicationService, ContextEngine)
            │
            ▼
[ Domain Layer ]
  (AcademicActivity, Resource, TimeSlot, Specifications, ScoringEngine)
            │
            ▼
[ Repository Interfaces ]
  (ResourceRepository, AllocationRepository, AllocationRunRepository, etc.)
            │
            ▼
[ Infrastructure Layer ]
  (GenericDao, JPA Entities, Mappers, Hibernate ORM, JpaUtil)
            │
            ▼
[ PostgreSQL Database ]
```

---

## 4. Resource & Academic Hierarchy

### Resource Hierarchy
```
Site (Masoro, Gishushu)
  └── Building (Science & Tech, Administration, Theology, Business)
        └── Resource (Computer Lab, Lecture Hall, Classroom)
```

### Academic Hierarchy
```
Department (CS & IT, Business)
  └── Program (Software Engineering, IT, BIS)
        └── Course (INSY 321, INSY 224, INSY 411)
              └── CourseOffering (Section A, 2026-S2)
                    └── AcademicActivity (Web Tech Lab, DB Lecture)
```

---

## 5. Technology Baseline
* **Java**: 21 LTS
* **Framework**: Jakarta EE 10 / JSF 4.0
* **Persistence**: Hibernate ORM 7.0 / JPA 3.2 (`RESOURCE_LOCAL`)
* **Database**: PostgreSQL 17
* **Containerization**: Docker & Docker Compose
* **Build System**: Apache Maven
