# AURA Security Architecture & Vulnerability Policy

[![Security](https://img.shields.io/badge/Security-Policy%20v1.0-blue)](SECURITY.md)
[![Vulnerability Disclosure](https://img.shields.io/badge/Disclosure-Responsible%20Policy-green)](SECURITY.md#1-vulnerability-reporting-policy)
[![OWASP](https://img.shields.io/badge/OWASP-Top%2010%20Compliant-brightgreen)](https://owasp.org/www-project-top-ten/)

This document outlines security boundaries, vulnerability disclosure procedures, threat mitigation architectures, and the Role-Based Access Control (RBAC) matrix for the **AURA** platform.

---

## 1. Vulnerability Reporting Policy

The AURA security team takes security vulnerabilities seriously. We appreciate responsible disclosure from researchers and the open-source community.

### 1.1 Supported Versions
| Version | Supported | Notes |
|---|---|---|
| `1.0.x` | :white_check_mark: | Current active release branch (`master` / `main`). |
| `< 1.0` | :x: | Experimental development builds. |

### 1.2 Reporting a Vulnerability
* **Do NOT report security vulnerabilities via public GitHub issues.**
* Please send security reports via encrypted email to: **`security@auca.ac.rw`** (or contact institutional maintainers directly).
* Include:
  1. Description of the vulnerability and attack vector.
  2. Steps to reproduce or proof-of-concept payload.
  3. Potential institutional impact (e.g. data leak, timetable sabotage).
* The team will acknowledge receipt within **48 hours** and provide a mitigation timeline.

---

## 2. STRIDE Threat Model & Mitigations

| Threat Category | Potential Attack Vector | AURA Architectural Mitigation |
|---|---|---|
| **Spoofing** | Impersonating administrative users to trigger solver or alter policies. | Session validation on CDI backing beans; Phase 3 LDAP / OAuth2 integration. |
| **Tampering** | Modifying published timetable allocations directly in transit or DB. | Parameterized JPA persistence; transactions restricted to `RESOURCE_LOCAL` application service boundaries. |
| **Repudiation** | Denying an unauthorized schedule commitment or room reallocation. | Full explainability audit logs in `aura_allocation_decisions` capturing `executed_by`, `committed_by`, and timestamps. |
| **Information Disclosure** | Exposing student personal identifiers or uncommitted proposals. | Separation of student schedule explorer (`student.xhtml` displaying only `COMMITTED` status) from admin console (`admin.xhtml`). |
| **Denial of Service** | Flooding optimization solver with exponential candidate loops. | Demand sorting and candidate generation bounds ($\mathcal{O}(N \cdot M \cdot K)$); solver timeout thresholds. |
| **Elevation of Privilege**| Unauthorized execution of `commitRun()` or policy updates. | Dual-layer presentation isolation and transactional revalidation guards. |

---

## 3. Defense-in-Depth Implementation

### 3.1 SQL Injection Prevention
* All database interactions are mediated exclusively through **Hibernate ORM / JPA 3.2** utilizing parameterized JPQL queries and typed `CriteriaBuilder` predicates.
* Dynamic query string concatenation is strictly prohibited across all repositories (`GenericDao<T, ID>`).

### 3.2 Cross-Site Scripting (XSS) & UI Defenses
* Jakarta Faces (JSF 4.0) Facelets auto-escapes all output variables (`#{adminBean.xxx}`) by default.
* Content Security Policy (CSP) headers and secure cookie flags (`HttpOnly`, `SameSite=Strict`) are enforced at the reverse proxy layer.

### 3.3 Cross-Site Request Forgery (CSRF)
* JSF includes cryptographic ViewState tokens in all POST forms, preventing external domains from submitting forged administrative requests.

### 3.4 Concurrency Race Exploit Defenses
* Double-booking exploits attempting simultaneous allocations are neutralized by the two-phase dynamic revalidation in `AllocationApplicationService.commitRun()`, executing atomic checks inside PostgreSQL transactions.

---

## 4. Phase 3 Role-Based Access Control (RBAC) Matrix

| Operational Action | System Admin | Academic Dean | Department Chair | Faculty Member | Student |
|---|:---:|:---:|:---:|:---:|:---:|
| **Execute Optimization Solver** | :white_check_mark: | :white_check_mark: | :x: | :x: | :x: |
| **Commit Proposed Run** | :white_check_mark: | :white_check_mark: | :x: | :x: | :x: |
| **Configure Policy Scoring Weights**| :white_check_mark: | :white_check_mark: | :x: | :x: | :x: |
| **Manage Room Assets & Capabilities**| :white_check_mark: | :x: | :x: | :x: | :x: |
| **Submit Academic Activity Demands** | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x: | :x: |
| **View Personal Timetable** | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x: |
| **Explore Campus Timetables** | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| **Access Audit Decision Traces** | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x: | :x: |
