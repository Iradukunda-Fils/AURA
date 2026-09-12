# Contributing to AURA

Thank you for your interest in contributing to the **AURA (AUCA Resource Allocation & Optimization System)** platform! AURA is an open-source institutional timetable optimization platform engineered for higher education institutions.

To maintain the highest level of software craftsmanship, architectural purity, and mathematical correctness, we ask all contributors to review and adhere to the following guidelines.

---

## 1. Code of Conduct

All contributors and maintainers are expected to uphold a respectful, inclusive, and professional environment. Please read and abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

---

## 2. Architectural Guardrails & Coding Standards

Before authoring code, you must understand our core architectural invariants:

### 2.1 Domain Purity Invariant
* **Zero Infrastructure in Domain**: Classes under `rw.ac.auca.aura.domain.*` must remain 100% pure Java. Never add `@Entity`, `@Table`, `@Inject`, `@Named`, or any Jakarta/Hibernate annotations to domain models.
* **Immutability by Default**: Domain value objects (`TimeSlot`, `Capability`, `CapabilitySet`, `ScoreBreakdown`) must be immutable with defensive copies of collections.
* **Rich Domain Logic**: Business validation rules must reside within domain aggregates, specifications, or scoring engines—not leaked into presentation beans.

### 2.2 Specification Pattern
* All feasibility checks must implement `rw.ac.auca.aura.domain.constraint.ResourceSpecification`.
* Specifications must return a `ConstraintResult` containing a boolean status (`SATISFIED` vs `VIOLATED`) and an informative, human-readable violation message to feed the Explainability Engine.

### 2.3 Persistence & Mapping
* Relational database tables must prefix with `aura_` and follow `snake_case` naming conventions.
* Entity classes must reside in `rw.ac.auca.aura.infrastructure.persistence.entities` and end with the `Entity` suffix.
* All conversions between Domain Models and JPA Entities must occur through explicit bi-directional mappers under `rw.ac.auca.aura.infrastructure.persistence.mappers`.

### 2.4 Modern Java 21 Idioms
* Utilize pattern matching for `instanceof` and switch expressions.
* Leverage Java `record` types where appropriate for immutable data carriers and DTOs.
* Prefer standard Java streams and immutable collections (`List.of()`, `Set.copyOf()`).

---

## 3. Development Workflow

We follow a standard Git branching and review workflow:

```
main / master (Protected: stable production releases)
    ▲
    │ (Squash & Merge via Pull Request)
feature/xyz or fix/xyz (Topic branch)
```

### 3.1 Step-by-Step Contribution Flow
1. **Fork or Branch**: Create a descriptive feature branch from `master`:
   ```bash
   git checkout -b feature/lecturer-consecutive-hour-spec
   ```
2. **Implement & Test**: Write unit and integration tests covering positive, boundary, and failure conditions.
3. **Verify Local Build**: Ensure the entire test suite compiles and executes with zero failures:
   ```bash
   mvn clean test
   ```
4. **Commit with Conventional Commits**: Format your commit messages according to the [Conventional Commits](https://www.conventionalcommits.org/) specification:
   ```
   feat: add maximum consecutive teaching hours constraint specification
   fix: prevent null pointer when evaluating optional room capabilities
   docs: update database ERD with allocation resource join entity
   test: add roundtrip persistence test for lecturer entity
   refactor: extract candidate filter to CandidateGenerator utility
   ```
5. **Open a Pull Request**: Push your branch and open a PR targeting `master`. Fill out the Pull Request template thoroughly.

---

## 4. Testing Requirements

A PR will not be approved without comprehensive test coverage:

* **Unit Tests**: Place in `src/test/java/rw/ac/auca/aura/domain/`. Validate mathematical formulas, constraint specifications, and time interval overlaps in isolation.
* **Mapper Roundtrip Tests**: Place in `src/test/java/rw/ac/auca/aura/infrastructure/persistence/`. Verify that Domain $\longleftrightarrow$ Entity conversions preserve all attributes losslessly.
* **Concurrency & Transaction Tests**: Verify that transactional boundary methods (`commitRun()`) gracefully detect and abort race conditions.

---

## 5. Pull Request Review Checklist

Before marking your PR as ready for review, verify:

- [ ] `mvn clean test` completes with `BUILD SUCCESS` (all 16+ tests passing).
- [ ] No JPA annotations exist in `domain/` packages.
- [ ] New database columns or tables are documented in [DATABASE.md](DATABASE.md).
- [ ] New constraint specifications are registered in [ALLOCATION_ENGINE.md](ALLOCATION_ENGINE.md).
- [ ] Code is formatted cleanly without extraneous IDE files or unused imports.
- [ ] Commit history is clean, readable, and follows conventional commits.
