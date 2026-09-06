package rw.ac.auca.aura.domain.academic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing an individual student.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentNumber;
    private final String name;
    private final String email;
    private final String cohortId;

    public Student(String id, String studentNumber, String name, String email, String cohortId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        this.id = id;
        this.studentNumber = studentNumber != null ? studentNumber : id;
        this.name = name;
        this.email = email != null ? email : "";
        this.cohortId = cohortId != null ? cohortId : "";
    }

    public String getId() { return id; }
    public String getStudentNumber() { return studentNumber; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCohortId() { return cohortId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + studentNumber + ")";
    }
}
