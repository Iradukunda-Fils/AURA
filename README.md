# AURA — AUCA Resource Allocation & Optimization System

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)](file:///home/iradukunda/Lost/Learn/Auca-Innovation/JAVA/WebTeck/aura)
[![Architecture](https://img.shields.io/badge/Architecture-Modular--Monolith-blue)](file:///home/iradukunda/Lost/Learn/Auca-Innovation/JAVA/WebTeck/aura/ARCHITECTURE.md)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange)](https://jdk.java.net/21/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-purple)](https://jakarta.ee/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)](https://www.postgresql.org/)

**AURA (AUCA Resource Allocation & Optimization System)** is an institutional decision-support and timetable optimization platform engineered for the Adventist University of Central Africa (AUCA).

---

## ⚡ Fundamental Booking vs. Optimization Distinction

> **Booking asks**: *"Is Room 101 available on Monday at 08:00?"*
>
> **AURA asks**: *"Given all competing academic demands, course offerings, lecturer constraints, student cohort schedules, required room capabilities, physical campus locations, and institutional priorities—what is the optimal, conflict-free institutional timetable?"*

AURA evaluates university resources holistically to prevent double-booking, eliminate lecturer/cohort overlaps, enforce capacity limits, and maximize student/faculty preference satisfaction.

---

## 🏗 System Architecture

AURA is implemented as a **Modular Monolith** with strict architectural separation between domain business rules, application use cases, repository interfaces, and database infrastructure.

```
[ JSF Presentation Layer ] ──> [ Application Services ] ──> [ Domain Models ]
                                                                   │
                                                                   ▼
[ PostgreSQL Database ] <── [ JPA / Hibernate ORM ] <── [ Repository Implementations ]
```

### Key Architectural Highlights
* **Domain Purity**: All domain models in `rw.ac.auca.aura.domain` are 100% free of JPA annotations or infrastructure code.
* **AcademicActivity as Demand Unit**: Demands encapsulate course offerings, student cohorts, assigned faculty, duration, required capabilities, and preferred timeslots.
* **Deterministic Optimization**: Hard constraints are enforced by `Specification` rules before calculating multi-objective soft scoring weights.
* **Transactional Concurrency Protection**: `commitRun()` dynamically revalidates active room status, capacity limits, and timeslot overlaps against live database allocations before commitment.

---

## 🚀 Quick Start Guide

### Prerequisites
* JDK 21 LTS
* Apache Maven 3.8+
* Docker & Docker Compose

### 1. Build Project
```bash
mvn clean package
```

### 2. Run Unit & Integration Tests
```bash
mvn test
```

### 3. Launch Docker Environment
```bash
docker compose up --build -d
```
Access the application dashboard at `http://localhost:8080/aura`

---

## 📚 Technical Documentation Directory

* 🏛 [ARCHITECTURE.md](ARCHITECTURE.md) — Modular monolith structure, dependency map, component hierarchy.
* 🗄 [DATABASE.md](DATABASE.md) — PostgreSQL ER schema, table specifications, indexing, constraint definitions.
* 🧠 [ALLOCATION_ENGINE.md](ALLOCATION_ENGINE.md) — Multi-stage optimization pipeline, hard constraint specifications, soft scoring formula.
* 🛠 [DEVELOPMENT.md](DEVELOPMENT.md) — Developer setup, environment configuration, Maven & Docker commands.
* 🔒 [SECURITY.md](SECURITY.md) — Security boundaries, Phase 3 planned RBAC matrix.
* ⚙️ [OPERATIONS.md](OPERATIONS.md) — Deployment targets, database backup/restore commands, logging strategy.
* 📜 [ADRS.md](docs/architecture/ADRS.md) — Architecture Decision Records (ADR-001 through ADR-010).

---

## 📄 License
Copyright © 2026 Adventist University of Central Africa (AUCA). All rights reserved.
