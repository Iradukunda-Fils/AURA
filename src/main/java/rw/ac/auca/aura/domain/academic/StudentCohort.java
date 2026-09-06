package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Aggregate Root representing a student cohort group attending shared core curriculum.
 * Essential for detecting student schedule collisions across course offerings.
 */
public class StudentCohort implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String programId;
    private final int academicYear;
    private final int studentCount;

    public StudentCohort(String id, String name, String programId, int academicYear, int studentCount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Cohort ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cohort name cannot be empty");
        }
        if (studentCount <= 0) {
            throw new IllegalArgumentException("Student count must be positive");
        }
        this.id = id;
        this.name = name;
        this.programId = programId != null ? programId : "";
        this.academicYear = academicYear;
        this.studentCount = studentCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProgramId() { return programId; }
    public int getAcademicYear() { return academicYear; }
    public int getStudentCount() { return studentCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCohort that = (StudentCohort) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + studentCount + " students)";
    }
}
