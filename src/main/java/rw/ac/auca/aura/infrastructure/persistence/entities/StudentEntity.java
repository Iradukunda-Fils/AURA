package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_students", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_number", columnNames = {"student_number"})
})
public class StudentEntity {

    @Id
    private String id;

    @Column(name = "student_number", nullable = false, length = 50)
    private String studentNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_cohort"))
    private StudentCohortEntity cohort;

    public StudentEntity() {}

    public StudentEntity(String id, String studentNumber, String fullName, String email, StudentCohortEntity cohort) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.email = email;
        this.cohort = cohort;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public StudentCohortEntity getCohort() { return cohort; }
    public void setCohort(StudentCohortEntity cohort) { this.cohort = cohort; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentEntity that = (StudentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
