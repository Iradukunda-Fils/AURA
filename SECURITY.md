# AURA Security & RBAC Specification

## 1. Security Architecture & Status
For Phase 2, authentication and user access control are intentionally deferred to Phase 3. However, all domain and application boundaries enforce structural authorization barriers:

1. **Presentation Boundary**: Read-only schedule exploration (`StudentIntentBean`) vs administrative optimization controls (`AdminAllocationBean`).
2. **Persistence Boundary**: Database user credentials (`aura` user) are restricted to the `auradb` database instance.
3. **Transaction Safeguards**: Database mutations occur strictly through transactional application service methods (`commitRun`, `updateWeightPolicy`).

---

## 2. Phase 3 Planned RBAC Matrix

| Role | Operational Scope | Access Boundary |
|---|---|---|
| **System Admin** | Full optimization pass execution, policy weight configuration, run commitment, master data CRUD | `admin.xhtml`, `rooms.xhtml`, `courses.xhtml`, `lecturers.xhtml`, `runs.xhtml` |
| **Department Chair** | Submit academic activity demands, set lecturer/cohort preferences | Activity submission forms |
| **Faculty Member** | View assigned personal timetable, report room equipment issues | Personal schedule portal |
| **Student** | Search committed timetables, filter campus schedules | `student.xhtml` |
