package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing an academic lecturer/instructor.
 */
public class Lecturer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String staffNumber;
    private final String name;
    private final String email;
    private final String departmentId;

    public Lecturer(String id, String staffNumber, String name, String email, String departmentId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer name cannot be empty");
        }
        this.id = id;
        this.staffNumber = staffNumber != null ? staffNumber : id;
        this.name = name;
        this.email = email != null ? email : "";
        this.departmentId = departmentId != null ? departmentId : "";
    }

    public String getId() { return id; }
    public String getStaffNumber() { return staffNumber; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartmentId() { return departmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecturer lecturer = (Lecturer) o;
        return Objects.equals(id, lecturer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + staffNumber + ")";
    }
}
