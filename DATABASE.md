# AURA Database Schema & Persistence Guide

## 1. Entity Relationship Model Overview
AURA uses a normalized PostgreSQL relational database (`auradb`) managed via JPA entities under `rw.ac.auca.aura.infrastructure.persistence.entities`.

```
aura_sites ◄────── aura_buildings ◄────── aura_resources
                                                ▲
                                                │ (M2M)
aura_allocation_runs ◄── aura_allocations ──────┴── aura_allocation_resources
        │
        └── aura_allocation_decisions

aura_departments ◄── aura_programs ◄── aura_courses ◄── aura_course_offerings ◄── aura_academic_activities
```

---

## 2. Table Specifications

### 2.1 Physical Spatial Tables
* **`aura_sites`**: `id` (PK), `name`, `location_code`
* **`aura_buildings`**: `id` (PK), `name`, `site_id` (FK → `aura_sites.id`)
* **`aura_resources`**: `id` (PK), `name`, `type`, `capacity`, `building_id` (FK → `aura_buildings.id`), `site_id` (FK), `status`, `capabilities_json`

### 2.2 Academic Tables
* **`aura_departments`**: `id` (PK), `name`, `faculty`
* **`aura_programs`**: `id` (PK), `name`, `department_id` (FK → `aura_departments.id`)
* **`aura_courses`**: `id` (PK), `code` (UNIQUE), `title`, `credit_hours`, `program_id` (FK)
* **`aura_lecturers`**: `id` (PK), `staff_number` (UNIQUE), `full_name`, `email`, `department_id` (FK)
* **`aura_student_cohorts`**: `id` (PK), `name`, `program_id` (FK), `academic_year`, `student_count`
* **`aura_students`**: `id` (PK), `student_number` (UNIQUE), `full_name`, `email`, `cohort_id` (FK)
* **`aura_course_offerings`**: `id` (PK), `course_id` (FK), `academic_year`, `semester`, `section`
* **`aura_offering_lecturers`**: `offering_id` (FK), `lecturer_id` (FK)
* **`aura_offering_cohorts`**: `offering_id` (FK), `cohort_id` (FK)
* **`aura_academic_activities`**: `id` (PK), `offering_id` (FK), `title`, `activity_type`, `student_count`, `duration_minutes`, `required_capabilities_json`, `preferred_site_id`, `preferred_day_of_week`, `preferred_start_time`, `preferred_end_time`, `state`
* **`aura_activity_lecturers`**: `activity_id` (FK), `lecturer_id` (FK)
* **`aura_activity_cohorts`**: `activity_id` (FK), `cohort_id` (FK)

### 2.3 Optimization & Allocation Tables
* **`aura_allocation_runs`**: `run_id` (PK), `policy_version`, `strategy_version`, `executed_at`, `executed_by`, `global_utility_score`, `status`
* **`aura_allocation_decisions`**: `id` (BIGINT PK), `run_id` (FK → `aura_allocation_runs.run_id`), `activity_id`, `selected_resource_id`, `selected_site_id`, `day_of_week`, `start_time`, `end_time`, `feasible`, `score`, `human_explanation`, `score_breakdown_json`, `constraint_results_json`, `rejected_alternatives_json`
* **`aura_allocations`**: `id` (PK), `run_id`, `activity_id`, `site_id`, `day_of_week`, `start_time`, `end_time`, `committed_at`, `committed_by`, `status`
* **`aura_allocation_resources`**: `id` (BIGINT PK), `allocation_id` (FK → `aura_allocations.id`), `resource_id` (FK → `aura_resources.id`)

### 2.4 Governance & Metadata Tables
* **`aura_policies`**: `id` (PK), `version` (UNIQUE), `description`, `weight_continuity`, `weight_proximity`, `weight_utilization`, `weight_preference`, `weight_load_balance`, `active`
* **`aura_system_metadata`**: `metadata_key` (PK), `metadata_value`, `updated_at`

---

## 3. Indexes & Query Performance
```sql
CREATE INDEX idx_alloc_time ON aura_allocations (day_of_week, start_time, end_time);
CREATE INDEX idx_alloc_status ON aura_allocations (status);
CREATE INDEX idx_resource_site ON aura_resources (site_id);
CREATE INDEX idx_resource_type ON aura_resources (type);
CREATE INDEX idx_activity_state ON aura_academic_activities (state);
```
