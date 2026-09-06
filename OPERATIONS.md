# AURA Operational & Deployment Guide

## 1. Production Deployment Target
AURA is packaged as a standard Jakarta EE WAR archive (`aura.war`) deployed to Apache Tomcat 10+ with Java 21 LTS runtime.

---

## 2. Database Backup & Maintenance

### 2.1 Dump PostgreSQL Database
```bash
pg_dump -h localhost -U aura -d auradb -F c -b -v -f auradb_backup_$(date +%Y%m%d).dump
```

### 2.2 Restore PostgreSQL Database
```bash
pg_restore -h localhost -U aura -d auradb -v auradb_backup_20260906.dump
```

---

## 3. Observability & Logging
Logs are emitted via standard Java Logging (`java.util.logging`) and Hibernate SQL loggers:

* **Seed & Initialization**: `rw.ac.auca.aura.infrastructure.persistence.DatabaseInitializer`
* **Allocation Execution**: `rw.ac.auca.aura.application.allocation.AllocationApplicationService`
* **Hibernate SQL Queries**: Enabled via `<property name="hibernate.show_sql" value="true"/>` in `persistence.xml`
