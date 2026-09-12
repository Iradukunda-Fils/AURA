# AURA Operations, Deployment & Runbook Manual

[![Runtime](https://img.shields.io/badge/Runtime-Apache%20Tomcat%2010.1-F8DC75?logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%2017-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

This manual defines operational deployment topologies, JVM garbage collection tuning, container health checks, automated backup/restore runbooks, and disaster recovery targets for **AURA**.

---

## 1. Production Architecture & Topology

```
                  Internet / AUCA Campus Network
                                │
                                ▼
         ┌─────────────────────────────────────────────┐
         │       TLS Termination & Reverse Proxy       │
         │          (Nginx / Traefik / Caddy)          │
         └──────────────────────┬──────────────────────┘
                                │ HTTPS (Port 443) -> HTTP (Port 8080)
                                ▼
         ┌─────────────────────────────────────────────┐
         │     AURA Web Container (Tomcat 10.1)        │
         │   Java 21 LTS • Jakarta EE 10 / JSF 4.0     │
         └──────────────────────┬──────────────────────┘
                                │ JDBC (Port 5432)
                                ▼
         ┌─────────────────────────────────────────────┐
         │      Database Container (PostgreSQL 17)     │
         │      Encrypted Persistent Storage Volume    │
         └─────────────────────────────────────────────┘
```

---

## 2. JVM Tuning & Container Resource Allocation

For reliable performance under intensive heuristic optimization solver runs, configure Tomcat's JVM arguments in `setenv.sh` or `CATALINA_OPTS`:

```bash
# Production JVM Flags for Java 21 LTS
CATALINA_OPTS="-server \
  -Xms1024m \
  -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:+AlwaysPreTouch \
  -XX:+ExitOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/aura/heapdump.hprof \
  -Djava.awt.headless=true \
  -Dfile.encoding=UTF-8"
```

### Resource Quotas (Docker Compose)
```yaml
services:
  aura-web:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 2560M
        reservations:
          cpus: '1.0'
          memory: 1024M
```

---

## 3. Container Health Checks & Liveness Probes

The Docker Compose stack specifies health checks to ensure dependent services initialize gracefully:

```yaml
services:
  postgres:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U aura -d auradb"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  aura-web:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/aura/index.xhtml"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
```

---

## 4. Database Backup & Disaster Recovery

### 4.1 Automated Nightly Backup Script
Save as `/opt/aura/scripts/backup_auradb.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

BACKUP_DIR="/var/backups/auradb"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/auradb_${TIMESTAMP}.dump"

mkdir -p "${BACKUP_DIR}"

# Execute custom binary compressed dump
docker exec aura-db pg_dump -U aura -d auradb -F c -b -v > "${BACKUP_FILE}"

# Retain backups for 30 days
find "${BACKUP_DIR}" -type f -name "auradb_*.dump" -mtime +30 -delete

echo "Backup completed: ${BACKUP_FILE}"
```

### 4.2 Full Database Restoration Runbook
To restore from a backup file during a disaster recovery event:

```bash
# 1. Stop application traffic to prevent concurrent mutations
docker compose stop aura-web

# 2. Terminate active PostgreSQL connections
docker exec -i aura-db psql -U aura -d postgres -c \
  "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'auradb' AND pid <> pg_backend_pid();"

# 3. Drop and recreate clean database
docker exec -i aura-db psql -U aura -d postgres -c "DROP DATABASE IF EXISTS auradb;"
docker exec -i aura-db psql -U aura -d postgres -c "CREATE DATABASE auradb OWNER aura;"

# 4. Restore schema and data
cat /var/backups/auradb/auradb_20260906_030000.dump | docker exec -i aura-db pg_restore -U aura -d auradb -v

# 5. Restart application container
docker compose start aura-web

# 6. Verify seed version and row counts
docker exec -i aura-db psql -U aura -d auradb -c "SELECT * FROM aura_system_metadata;"
```

---

## 5. Service Level Objectives (SLOs)

* **Recovery Point Objective (RPO)**: $< 24\text{ hours}$ (nightly automated snapshots).
* **Recovery Time Objective (RTO)**: $< 15\text{ minutes}$ (full containerized restore).
* **Availability**: 99.9% uptime during active semester scheduling windows.

---

## 6. Observability & Log Management

AURA logs events to standard container stdout/stderr. Key logger namespaces:

| Logger Name | Level | Purpose |
|---|---|---|
| `rw.ac.auca.aura.infrastructure.persistence.DatabaseInitializer` | `INFO` | Seed verification and initial data load logs. |
| `rw.ac.auca.aura.application.allocation.AllocationApplicationService` | `INFO` | Execution timing, candidate count, and commit events. |
| `rw.ac.auca.aura.domain.allocation.GreedyPriorityStrategy` | `FINE` | Deep solver step evaluations and score calculations. |
| `org.hibernate.SQL` | `DEBUG` (Dev) / `WARN` (Prod) | SQL query inspection. |
