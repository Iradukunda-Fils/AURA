package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing an academic department.
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String code;

    public Department(String id, String name, String code) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.code = code != null ? code : id;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
