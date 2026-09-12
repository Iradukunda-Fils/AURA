# AURA Developer Onboarding & Engineering Guide

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

Welcome to the **AURA** engineering team! This guide details local workstation setup, IDE configuration, test execution, container orchestration, database inspection, and coding standards.

---

## 1. Workstation Prerequisites

Ensure your development environment meets the following baseline:

* **Java Development Kit (JDK)**: OpenJDK 21 LTS (Amazon Corretto, Eclipse Temurin, or Oracle JDK 21).
* **Build System**: Apache Maven 3.8.6+ (or Maven 3.9+).
* **Container Runtime**: Docker Desktop or Docker Engine 24.0+ with Docker Compose v2.
* **Database Client (Optional)**: DBeaver, pgAdmin 4, DataGrip, or CLI `psql`.
* **Git**: Git 2.38+ with line-ending configuration (`core.autocrlf = input` on Unix/macOS, `true` on Windows).

---

## 2. Environment Configuration

AURA persists data via JPA (`RESOURCE_LOCAL`). Configuration parameters can be overridden at runtime via system environment variables:

| Environment Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` (Local) / `postgres` (Docker) | PostgreSQL database hostname. |
| `DB_PORT` | `5432` | PostgreSQL listener port. |
| `DB_NAME` | `auradb` | Target PostgreSQL database name. |
| `DB_USER` | `aura` | Database user account. |
| `DB_PASS` | `aurapassword` | Database password. |

---

## 3. IDE Setup & Configuration

### 3.1 IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA $\to$ **File** $\to$ **Open...** $\to$ Select the project root folder (`pom.xml`).
2. Navigate to **File** $\to$ **Project Structure...** $\to$ **Project**:
   * Set **SDK** to `21 (java version "21.0.x")`.
   * Set **Language Level** to `21 - Pattern matching, record patterns, sequenced collections`.
3. Under **Preferences / Settings** $\to$ **Build, Execution, Deployment** $\to$ **Build Tools** $\to$ **Maven**:
   * Ensure Maven home directory points to Maven 3.8+.
   * Enable **Import Maven projects automatically**.

### 3.2 Visual Studio Code
1. Install the **Extension Pack for Java** (Microsoft) and **Docker** extension.
2. In `.vscode/settings.json`, set:
   ```json
   {
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-21",
         "path": "/path/to/jdk-21",
         "default": true
       }
     ]
   }
   ```

---

## 4. Maven Build & Testing Commands

### 4.1 Compile & Validate
```bash
# Clean previous builds and compile all source files
mvn clean compile
```

### 4.2 Execute Test Suite
```bash
# Run all unit, integration, and roundtrip tests
mvn test

# Run a specific test class in isolation
mvn test -Dtest=AllocationEngineTest

# Run a specific test method
mvn test -Dtest=SpecificationTest#testCapacitySpecificationFailsWhenOversized
```

### 4.3 Package Production WAR
```bash
# Compile, run test assertions, and generate target/aura.war
mvn clean package
```

---

## 5. Docker Local Development Environment

Docker Compose manages a localized production-parity environment running PostgreSQL 17 and Apache Tomcat 10:

```bash
# 1. Build WAR image and launch services in background
docker compose up --build -d

# 2. Tail unified container output
docker compose logs -f

# 3. View status of active services
docker compose ps

# 4. Stop containers while preserving database volume
docker compose stop

# 5. Destroy containers and reset database volume
docker compose down -v
```

Once running, access the web console at: **`http://localhost:8080/aura`**

---

## 6. Database Inspection & CLI Debugging

### 6.1 Connect via Docker CLI
To directly query the running PostgreSQL database inside Docker:

```bash
docker exec -it aura-db psql -U aura -d auradb
```

### 6.2 Useful Diagnostic Queries
```sql
-- Check active schema tables
\dt

-- Check database seed status
SELECT * FROM aura_system_metadata WHERE metadata_key = 'seed_version';

-- Check resource count by type and campus site
SELECT site_id, type, COUNT(*), SUM(capacity) as total_seats 
FROM aura_resources 
GROUP BY site_id, type;

-- Review committed timetable allocations
SELECT a.id, a.activity_id, a.day_of_week, a.start_time, a.end_time, r.resource_id
FROM aura_allocations a
JOIN aura_allocation_resources r ON a.id = r.allocation_id
ORDER BY a.day_of_week, a.start_time;

-- Inspect latest optimization run decisions
SELECT activity_id, selected_resource_id, feasible, score, human_explanation
FROM aura_allocation_decisions
ORDER BY id DESC
LIMIT 10;
```

---

## 7. Engineering Standards & Conventions

1. **Domain Purity Rule**: Never import `jakarta.persistence.*`, `jakarta.inject.*`, or any infrastructure code into `rw.ac.auca.aura.domain.*`.
2. **Repository Boundary**: Application services must only access domain models through repository interfaces (`rw.ac.auca.aura.domain.repository.*`).
3. **Lossless Mappers**: Every new entity field must have corresponding roundtrip unit test coverage in `MapperRoundtripTest`.
4. **Conventional Commits**: Format commit messages cleanly (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`).

