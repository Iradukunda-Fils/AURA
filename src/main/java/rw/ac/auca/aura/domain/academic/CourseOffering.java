package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain entity representing a specific delivery/section instance of a course.
 * Connects Course, Academic Term, Section, Lecturers, and Target Cohorts.
 */
public class CourseOffering implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String courseId;
    private final String term; // e.g. "2026-SEM1"
    private final String section; // e.g. "Section A"
    private final Set<String> lecturerIds;
    private final Set<String> cohortIds;

    public CourseOffering(String id, String courseId, String term, String section,
                          Set<String> lecturerIds, Set<String> cohortIds) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Course offering ID cannot be empty");
        }
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be empty");
        }
        this.id = id;
        this.courseId = courseId;
        this.term = term != null ? term : "";
        this.section = section != null ? section : "Section A";
        this.lecturerIds = lecturerIds != null ? Collections.unmodifiableSet(new HashSet<>(lecturerIds)) : Collections.emptySet();
        this.cohortIds = cohortIds != null ? Collections.unmodifiableSet(new HashSet<>(cohortIds)) : Collections.emptySet();
    }

    public String getId() { return id; }
    public String getCourseId() { return courseId; }
    public String getTerm() { return term; }
    public String getSection() { return section; }
    public Set<String> getLecturerIds() { return lecturerIds; }
    public Set<String> getCohortIds() { return cohortIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseOffering offering = (CourseOffering) o;
        return Objects.equals(id, offering.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return courseId + " (" + term + ", " + section + ")";
    }
}
