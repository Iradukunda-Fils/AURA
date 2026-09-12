# AURA — AUCA Resource Allocation & Optimization System

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square&logo=githubactions&logoColor=white)](https://github.com/Iradukunda-Fils/AURA)
[![Architecture](https://img.shields.io/badge/Architecture-Modular--Monolith-blue?style=flat-square)](ARCHITECTURE.md)
[![Database ERD](https://img.shields.io/badge/Database-PostgreSQL%2017%20ERD-336791?style=flat-square&logo=postgresql&logoColor=white)](DATABASE.md)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?style=flat-square&logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10%20Faces%204.0-purple?style=flat-square)](https://jakarta.ee/)
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM%207.0-59666C?style=flat-square&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![License](https://img.shields.io/badge/License-MIT%20%2F%20Proprietary-green?style=flat-square)](#-license)

**AURA (AUCA Resource Allocation & Optimization System)** is an institutional decision-support and timetable optimization platform engineered for the Adventist University of Central Africa (AUCA).

AURA solves multi-campus university resource contention by evaluating competing academic demands, course offerings, lecturer constraints, student cohort progression, specialized room equipment requirements, physical campus locations, and institutional policies to synthesize optimal, conflict-free institutional timetables.

---

## ⚡ Fundamental Paradigm: Booking vs. Optimization

The core distinction that separates AURA from standard room reservation systems:

> **Traditional Booking asks**:
> *"Is Computer Lab 1 available on Monday at 08:00?"*
> *(Local, first-come-first-served, reactive, blind to curriculum priorities and global fairness)*

> **AURA Optimization asks**:
> *"Given 150 competing course offerings, 45 faculty schedules, 12 student cohorts across 2 campuses, capacity constraints, specialized hardware requirements, and institutional policy weights—what is the mathematically optimal, conflict-free allocation of all university resources?"*
> *(Global, multi-objective, deterministic, constraint-satisfying, fully auditable)*

---

## 🏗 High-Level Architectural Blueprint

AURA is implemented as a **Clean / Hexagonal Modular Monolith** where pure domain rules are completely decoupled from persistence infrastructure:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        JAKARTA FACES 4.0 (JSF)                         │
│   Dashboard • Optimization Console • Timetable Explorer • Catalogs     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ CDI Invocations
┌───────────────────────────────────▼────────────────────────────────────┐
│                       APPLICATION SERVICES LAYER                       │
│    AllocationApplicationService • AcademicContextEngine • Tx Guards    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Pure Domain Contracts
┌───────────────────────────────────▼────────────────────────────────────┐
│                          DOMAIN CORE LAYER                             │
│   Hard Constraint Specifications  •  Multi-Objective Scoring Engine   │
│   Greedy Priority Strategy Solver •  Explainability & Audit Engine     │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ Implements SPI
┌───────────────────────────────────┴────────────────────────────────────┐
│                    PERSISTENCE & INFRASTRUCTURE LAYER                  │
│    GenericDao<T, ID> • 17 JPA Entities • Bi-directional Mappers        │
│    Hibernate ORM 7.0 • PostgreSQL 17 (auradb) • Idempotent Seeder      │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🌟 Key System Capabilities

* **100% Domain Purity**: Domain models in `rw.ac.auca.aura.domain.*` contain zero JPA annotations or framework dependencies.
* **Hard Constraint Specification Engine**: Zero double-booking, room capacity enforcement, required equipment matching, lecturer conflict elimination, and cohort conflict elimination guaranteed via the Specification pattern.
* **Multi-Objective Soft Scoring**: Balances student cohort continuity, inter-campus transit minimization, room utilization efficiency, faculty time preferences, and weekly load balance.
* **Full Explainability & Audit Trail**: Every allocation decision records a human-readable explanation narrative, a numerical score breakdown, and an explicit list of rejected alternative rooms with specific reasons.
* **Transactional Concurrency Protection**: Live schedule commitment in `commitRun()` revalidates active room status, capacity limits, and timeslot overlaps against PostgreSQL inside an atomic transaction before publishing.
* **High-Density Dark-Mode UI**: Built with vanilla CSS design system featuring a collapsible responsive sidebar, live KPI cards, and interactive schedule filtering.
* **Idempotent Database Seeding**: Automated master dataset bootstrap with metadata version tracking (`seed_version = "1.0"`).

---

## 🖥 Interactive Web Portals

AURA provides 7 purpose-built views accessible via the navigation sidebar:

| View | Path | Primary Purpose |
|---|---|---|
| **Executive Dashboard** | `/index.xhtml` | Real-time institutional KPI metrics, current allocation summary, and campus utilization overview. |
| **Optimization Console** | `/admin.xhtml` | Administrative solver execution, policy scoring weight configuration, and interactive decision explainability traces. |
| **Schedule Explorer** | `/student.xhtml` | Multi-filter weekly timetable viewer searchable by campus site, day of week, course, and cohort. |
| **Room Registry** | `/rooms.xhtml` | Physical facilities directory displaying room types, capacities, campus sites, and hardware capabilities. |
| **Course Catalog** | `/courses.xhtml` | Academic courses, credit hours, degree programs, and active semester offerings. |
| **Faculty Directory** | `/lecturers.xhtml` | Faculty roster, departmental affiliations, and teaching assignments. |
| **Run History** | `/runs.xhtml` | Immutable audit log of past optimization solver runs, global utility scores, and commitment statuses. |

---

## 🚀 Quick Start Guide

### Prerequisites
* **Java Development Kit**: JDK 21 LTS
* **Build System**: Apache Maven 3.8+
* **Container Engine**: Docker 24+ and Docker Compose 2.0+

### Option A: One-Command Docker Launch (Recommended)
Clone the repository and run Docker Compose to launch PostgreSQL 17 and Apache Tomcat 10:

```bash
# 1. Clone repository
git clone https://github.com/Iradukunda-Fils/AURA.git
cd AURA

# 2. Build and launch containers
docker compose up --build -d

# 3. Stream container logs
docker compose logs -f
```

Access the web platform at: **`http://localhost:8080/aura`**

### Option B: Local Maven Build & Test
```bash
# Compile and run all 16 unit, integration, and roundtrip tests
mvn clean test

# Build production WAR package
mvn clean package
```

---

## 📚 Technical Documentation Suite

The repository includes comprehensive architectural, database, operational, and security specifications:

| Document | Focus & Highlights |
|---|---|
| 🗄 **[DATABASE.md](DATABASE.md)** | **Exhaustive PostgreSQL 17 ERD**, 21-table data dictionary, 3NF normalization rationale, compound indexes, and ACID concurrency. |
| 🏛 **[ARCHITECTURE.md](ARCHITECTURE.md)** | C4 Context & Container models, Onion architecture layers, state machines, and sequence diagrams. |
| 🧠 **[ALLOCATION_ENGINE.md](ALLOCATION_ENGINE.md)** | Mathematical formulation of utility scoring, CSP constraint modeling, and heuristic solver complexity. |
| 🛠 **[DEVELOPMENT.md](DEVELOPMENT.md)** | Developer onboarding, IDE setup (IntelliJ / VS Code), Maven profiles, and test execution. |
| ⚙️ **[OPERATIONS.md](OPERATIONS.md)** | Production deployment topologies, JVM memory tuning for Java 21 G1GC, container health checks, and backups. |
| 🔒 **[SECURITY.md](SECURITY.md)** | STRIDE threat analysis, injection defenses, JSF XSS/CSRF mitigations, and Phase 3 RBAC roadmap. |
| 🤝 **[CONTRIBUTING.md](CONTRIBUTING.md)** | Open-source contributor guidelines, Conventional Commits standard, and PR review checklist. |
| 📜 **[ADRS.md](docs/architecture/ADRS.md)** | Architectural Decision Records (ADR-001 through ADR-010). |

---

## 🧪 Verification & Test Suite

AURA maintains rigorous test coverage spanning all architectural layers:

```bash
mvn test
```

* **`AllocationEngineTest`**: Verifies greedy priority strategy, hard constraint pruning, and multi-objective soft scoring.
* **`SpecificationTest`**: Verifies capacity limits, capability matching, availability, and lecturer/cohort conflict elimination.
* **`TimeSlotTest`**: Tests boundary conditions and interval overlap algebra across weekdays.
* **`Phase2BEntityTest`**: Validates JPA entity persistence, foreign key cascades, and table mapping integrity.
* **`MapperRoundtripTest`**: Ensures lossless bi-directional translation between pure Domain Models and JPA Entities.
* **`ConcurrencyRevalidationTest`**: Asserts that `commitRun()` dynamically catches and aborts concurrent room allocations.

---

## 🤝 Contributing

We welcome contributions from the academic and open-source software community! Please review our **[Contributing Guidelines](CONTRIBUTING.md)** and **[Code of Conduct](CODE_OF_CONDUCT.md)** before submitting a pull request.

---

## 📄 License

Copyright © 2026 Adventist University of Central Africa (AUCA).
Licensed under the [MIT License](LICENSE) (or institutional university terms).

