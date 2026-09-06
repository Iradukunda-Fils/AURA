# AURA Architecture Decision Records (ADRs)

This document records the binding architecture decision records for the AURA platform.

---

## ADR-001: Modular Monolith System Architecture

* **Context**: AURA is an institutional optimization system for university resource allocation. Microservices would introduce network serialization latency, distributed transaction complexity, and operational overhead without architectural benefits.
* **Decision**: Adopt a single-deployable Modular Monolith architecture on Jakarta EE / Java 21 LTS with clean package boundaries.
* **Consequences**: High development speed, simple transactional boundaries, clean in-memory optimization execution.

---

## ADR-002: Strict Separation of Domain Model and Persistence Entities

* **Context**: Mixing JPA annotations (`@Entity`, `@Table`) into domain objects breaks domain purity and pollutes business logic with database mapping concerns.
* **Decision**: Keep domain classes in `rw.ac.auca.aura.domain` completely free of `@Entity` or JPA annotations. Create separate JPA entities under `rw.ac.auca.aura.infrastructure.persistence.entities`. Use explicit Mappers for conversion.
* **Consequences**: Total infrastructure independence for domain logic; clear boundary for unit testing.

---

## ADR-003: Repository Interfaces Outside Infrastructure

* **Context**: Application services and domain logic should depend on high-level data abstractions, not specific database implementations or `EntityManager`.
* **Decision**: Place all repository interfaces in `rw.ac.auca.aura.domain.repository`. Implement them in `rw.ac.auca.aura.infrastructure.persistence.repositories` using `GenericDao` and JPA.
* **Consequences**: Decoupled presentation and application layers; domain code never touches JPA types.

---

## ADR-004: Tomcat + RESOURCE_LOCAL Transaction Model

* **Context**: AURA runs on Apache Tomcat rather than a full Jakarta EE Application Server (Payara/WildFly). Tomcat does not provide container-managed JTA transactions.
* **Decision**: Use `transaction-type="RESOURCE_LOCAL"` in `persistence.xml`. Manage `EntityManager` lifecycles and transaction boundaries at the Application Service level using `JpaUtil`.
* **Consequences**: Lightweight deployment target; explicit use-case transaction boundaries (`begin`, `commit`, `rollback`).

---

## ADR-005: Normalized Multi-Resource Allocation Persistence

* **Context**: The domain `Allocation` represents allocations as `Set<String> resourceIds`, supporting multi-resource scenarios (e.g. adjacent exam halls).
* **Decision**: Persist multi-resource relationships using a normalized join table `aura_allocation_resources` (`allocation_id`, `resource_id`) rather than storing serialized JSON arrays.
* **Consequences**: Relational integrity and indexable queries in PostgreSQL for multi-room allocations.

---

## ADR-006: AcademicActivity as the Core Allocation Demand Unit

* **Context**: University resource allocation is not merely room booking. Demands originate from course offerings involving lecturers, student cohorts, required capabilities, duration, and preferred times.
* **Decision**: Model `AcademicActivity` as the primary schedulable demand unit rather than a simple string request.
* **Consequences**: Captures full institutional context for multi-objective optimization.

---

## ADR-007: Versioned Allocation Policy & Dynamic Weight Control

* **Context**: Institutional priorities change (e.g. emphasizing faculty preference during midterm vs room utilization during finals).
* **Decision**: Model `AllocationPolicy` as a versioned aggregate containing multi-objective weights (Continuity, Proximity, Utilization, Preference, Load Balance).
* **Consequences**: Enables deterministic simulation passes under different policy configurations.

---

## ADR-008: Deterministic Heuristic Optimization Core

* **Context**: Resource allocation requires guaranteed constraint enforcement (no double-booking, capacity limits, lecturer availability). LLMs/AI cannot guarantee hard mathematical constraints.
* **Decision**: Keep the optimization engine deterministic (hard-constraint filtering via `SpecificationContext`, soft-scoring via `ScoringEngine`, greedy priority allocation via `GreedyPriorityStrategy`).
* **Consequences**: 100% reproducible, verifiable, and explainable allocation decisions.

---

## ADR-009: Idempotent Seed Initialization via System Metadata

* **Context**: Checking table counts (e.g. `count(sites) == 0`) is fragile for system initialization across restarts.
* **Decision**: Use `aura_system_metadata` table tracking `seed_version`. `DatabaseInitializer` executes seed logic only if the target version is absent.
* **Consequences**: Safe, idempotent application startup across development and production environments.

---

## ADR-010: Transactional Concurrency Revalidation on Commitment

* **Context**: Between simulation pass execution and live commitment, another administrator or run may have committed conflicting allocations or deactivated a room.
* **Decision**: `commitRun()` performs a mandatory final hard-constraint, active status, and timeslot overlap revalidation inside the commit boundary. Stale proposals are rejected.
* **Consequences**: Guarantees database state integrity and prevents stale double-booking under concurrent usage.
