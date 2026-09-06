package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing an abstract curriculum course item (e.g., CS301 - Database Systems).
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String code;
    private final String name;
    private final int credits;
    private final String programId;

    public Course(String id, String code, String name, int credits, String programId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.programId = programId != null ? programId : "";
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public String getProgramId() { return programId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
