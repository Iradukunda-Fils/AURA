package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing an academic program (e.g. Software Engineering).
 */
public class Program implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String departmentId;

    public Program(String id, String name, String departmentId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Program ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Program name cannot be empty");
        }
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartmentId() { return departmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return Objects.equals(id, program.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
