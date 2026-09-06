# AURA Developer & Setup Guide

## 1. Prerequisites
* **Java Development Kit (JDK)**: Java 21 LTS
* **Build Tool**: Apache Maven 3.8+
* **Database**: PostgreSQL 17 (or Docker Compose container)
* **Docker Engine & Docker Compose**: 24.0+

---

## 2. Environment Configuration
Environment variables supported by `JpaUtil`:

| Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `postgres` | PostgreSQL hostname (`localhost` for local run, `postgres` inside Docker) |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `auradb` | PostgreSQL database name |
| `DB_USER` | `aura` | Database username |
| `DB_PASS` | `aurapassword` | Database password |

---

## 3. Maven Build & Testing Commands
```bash
# Clean and compile project
mvn clean compile

# Run all unit and integration tests
mvn test

# Package WAR web archive
mvn clean package
```

---

## 4. Docker Local Setup
```bash
# Build Docker image and start PostgreSQL + Tomcat containers
docker compose up --build -d

# View container logs
docker compose logs -f

# Stop containers
docker compose down
```

Access Web UI at `http://localhost:8080/aura`
