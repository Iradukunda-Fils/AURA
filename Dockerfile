# ==========================================
# Stage 1: Build Application (Maven Stage)
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml to cache dependencies layer
COPY pom.xml .

# Download dependencies offline for faster iterative builds
RUN mvn dependency:go-offline -B

# Copy application source files
COPY src ./src

# Package application into WAR file
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime Environment (Tomcat Stage)
# ==========================================
FROM tomcat:11.0-jdk21-temurin-noble

LABEL maintainer="AURA Engineering Team <admin@auca.ac.rw>"
LABEL description="AURA - AUCA Resource Allocation & Optimization System"

# Remove default Tomcat sample applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR file from Stage 1 into Tomcat webapps directory as ROOT.war
# This deploys the application at the root context path (http://localhost/)
COPY --from=build /app/target/aura-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat internal port
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]
