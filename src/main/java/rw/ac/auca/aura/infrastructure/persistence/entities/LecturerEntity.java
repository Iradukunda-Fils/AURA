package rw.ac.auca.aura.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "aura_lecturers", uniqueConstraints = {
    @UniqueConstraint(name = "uk_lecturer_staff_number", columnNames = {"staff_number"})
})
public class LecturerEntity {

    @Id
    private String id;

    @Column(name = "staff_number", nullable = false, length = 50)
    private String staffNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lecturer_department"))
    private DepartmentEntity department;

    public LecturerEntity() {}

    public LecturerEntity(String id, String staffNumber, String fullName, String email, DepartmentEntity department) {
        this.id = id;
        this.staffNumber = staffNumber;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public DepartmentEntity getDepartment() { return department; }
    public void setDepartment(DepartmentEntity department) { this.department = department; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerEntity that = (LecturerEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
